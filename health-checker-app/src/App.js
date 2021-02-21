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
    {title: "id", field: "id"},
    {title: "name", field: "name"},
    {title: "url", field: "url"},
    {title: "status", field: "status"},
    {title: "createdAt", field: "createdAt"},
    {title: "updatedAt", field: "updatedAt"},
    {title: "userId", field: "userId"}
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
   * removes fields not ment to sent on writing calls
   * @param {*} service 
   */
  const sanitizeWriteParams = (service) => {
    const updateFields = {...service};
    delete updateFields.updatedAt;
    delete updateFields.createdAt;
    return updateFields;
  }

  const handleRowUpdate = (newData, oldData, resolve) => {
    let errorList = validateServiceFields(newData);
    const updateData = sanitizeWriteParams(newData)
    if(errorList.length < 1){
      api.put("/services/"+newData.id, updateData)
      .then(res => {
        const tmp = [...data];
        tmp[oldData.tableData.id] = res.data;
        setData([...tmp]);
        resolve()
        setIserror(false)
        setErrorMessages([])
      })
      .catch(error => {
        console.log(error);
        setErrorMessages(["Update failed! Server error"])
        setIserror(true)
        resolve()
      })
    }else{
      setErrorMessages(errorList)
      setIserror(true)
      resolve()
    }
    
  }

  const handleRowAdd = (newData, resolve) => {
    let errorList = validateServiceFields(newData);
    if(errorList.length < 1){
      api.post("/services", newData)
      .then(res => {
        let tmp = [...data];
        tmp.push(res.data);
        setData(tmp);
        resolve()
        setErrorMessages([])
        setIserror(false)
      })
      .catch(error => {
        console.log(error);
        setErrorMessages(["Cannot add data. Server error!"])
        setIserror(true)
        resolve()
      })
    }else{
      setErrorMessages(errorList)
      setIserror(true)
      resolve()
    }

    
  }

  const handleRowDelete = (oldData, resolve) => {
    api.delete("/services/"+oldData.id)
      .then(res => {
        const dataDelete = [...data];
        const index = oldData.tableData.id;
        dataDelete.splice(index, 1);
        setData([...dataDelete]);
        resolve()
      })
      .catch(error => {
        setErrorMessages(["Delete failed! Server error"])
        setIserror(true)
        resolve()
      })
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
                onRowUpdate: (newData, oldData) =>
                  new Promise((resolve) => {
                      handleRowUpdate(newData, oldData, resolve);
                  }),
                onRowAdd: (newData) =>
                  new Promise((resolve) => {
                    handleRowAdd(newData, resolve)
                  }),
                onRowDelete: (oldData) =>
                  new Promise((resolve) => {
                    handleRowDelete(oldData, resolve)
                  }),
              }}
              options={{
                paging: false
              }}
            />
    </div>
  );
}

export default App;