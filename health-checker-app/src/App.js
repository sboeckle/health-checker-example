import React, { useState, useEffect } from 'react';
import './App.css';
import { forwardRef } from 'react';

import MaterialTable from "material-table";
import AddBox from '@material-ui/icons/AddBox';
import ArrowDownward from '@material-ui/icons/ArrowDownward';
import Check from '@material-ui/icons/Check';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';
import axios from 'axios'
import Alert from '@material-ui/lab/Alert';

const tableIcons = {
  Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
  Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
  Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
  Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
  Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
  Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
  ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
  SortArrow: forwardRef((props, ref) => <ArrowDownward {...props} ref={ref} />),
  ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
  ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
};

const servicesBaseUrl = process.env.REACT_APP_SERVICE_BASE_URL
const api = axios.create({
  baseURL: servicesBaseUrl
})

function App() {
  var columns = [
    {title: 'id', field: 'id'},
    {title: 'name', field: 'name'},
    {title: 'url', field: 'url'},
    {title: 'userId', field: 'userId'},
    {title: 'status', field: 'status', editable: 'never'},
    {title: 'lastCheckedAt', field: 'lastCheckedAt', editable: 'never'},
    {title: 'createdAt', field: 'createdAt', editable: 'never'},
    {title: 'updatedAt', field: 'updatedAt', editable: 'never'}
  ]

  const [data, setData] = useState([]);
  const [iserror, setIserror] = useState(false)
  const [errorMessages, setErrorMessages] = useState([])

  useEffect(() => { 
    api.get("/services")
        .then(res => {           
            setData(res.data)
         })
         .catch(error=>{
             console.log("Error", error)
         })
  }, [])

  const validateServiceFields = (service) => {
    let errorList = []
    if(!service.id || service.id === ""){
      errorList.push("Please set id")
    }
    if(!service.name || service.name === ""){
      errorList.push("Please set name")
    }
    if(!service.url || service.url === ""){
      errorList.push("Please set url")
    }
    return errorList
  }

  /**
   * sanitizes data for a write request (POST & PUT)
   * i.e. deletes meta fields which are not allowed to be written by REST api
   * @param {*} newData 
   */
  const sanitizeWriteRequest = (newData) => {
    const serviceData = {...newData}
    delete serviceData.createdAt
    delete serviceData.updatedAt
    delete serviceData.lastCheckedAt
    return serviceData
  }

  const onRowUpdate = async (newData, oldData) => {
    try {
      let errorList = validateServiceFields(newData);
      const updateData = sanitizeWriteRequest(newData)
      if(errorList.length > 1){
        setErrorMessages(errorList)
        setIserror(true)
        return
      }
      const updatedService = await api.put("/services/" + newData.id, updateData)
      const tmp = [...data];
      tmp[oldData.tableData.id] = updatedService.data;
      setData([...tmp]);
      setIserror(false)
      setErrorMessages([])
    } catch(err) {
      console.log(err);
      setErrorMessages(["Update failed! Server error"])
      setIserror(true)
    }
  }

  const onRowAdd = async newData => {
    let errorList = validateServiceFields(newData);
    const serviceData = sanitizeWriteRequest(newData)
    if(errorList.length < 1){
      try {
        const res = await api.post("/services", serviceData)
        let tmp = [...data];
        tmp.push(res.data);
        setData(tmp);
        setErrorMessages([])
        setIserror(false)
      } catch(err) {
        console.log(err);
        setErrorMessages(["Cannot add data. Server error!"])
        setIserror(true)
      }
    } else{
      setErrorMessages(errorList)
      setIserror(true)
    }
  }

  const onRowDelete = async oldData => {
    try{
      await api.delete("/services/"+oldData.id)
      const dataDelete = [...data];
      dataDelete.splice(oldData.tableData.id, 1);
      setData([...dataDelete]);
    } catch (err) {
      console.log(err);
      setErrorMessages(["Delete failed! Server error"])
      setIserror(true)
    }
  }


  return (
    <div className="App">
          <div>
            {iserror && 
              <Alert severity="error">
                  {errorMessages.map((msg, i) => {
                      return <div key={i}>{msg}</div>
                  })}
              </Alert>
            }       
          </div>
            <MaterialTable
              title="Services Health Status"
              columns={columns}
              data={data}
              icons={tableIcons}
              editable={{
                onRowUpdate,
                onRowAdd,
                onRowDelete
              }}
              options={{
                paging: false
              }}
            />
    </div>
  );
}

export default App;