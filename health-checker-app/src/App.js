import React, {Component} from 'react';
import Services from './components/services'

class App extends Component {

  state = {
    services: []
  }
  componentDidMount() {
    console.log('did mount')
    const servicesBaseUrl = 'http://localhost:8080/api/'
    fetch(servicesBaseUrl + 'services', {
      crossDomain:true,
      headers: {'Content-Type':'application/json, "Access-Control-Allow-Origin": "*"'},
    })
    .then(res => res.json())
    .then(services => {
      console.log(services);
      this.setState({ services })
    })
    .catch(console.log)
  }
  render() {
    console.log('did render');
    return (
      <div className="App">
      <h1>Services Status</h1>
      <Services services={this.state.services} />
      </div>
    );
  }
}

export default App;
