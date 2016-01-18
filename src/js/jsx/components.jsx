var React = require('react');
var ReactDOM = require('react-dom');
var classNames = require('classnames');

var UserTable = React.createClass({
    render: function() {
        var ulist = this.props.users.filter(function(u) { return (u.balance > 0) });
        console.log(ulist);
        return (
            <div className="usertable">
                {ulist.map(function(user) {
                    return <UserRow key={user.id} user={user}/>;
                })}
            </div>
        );}
});

var UserRow = React.createClass({
    render: function() {
        return (
            <div className="container row">
                <span className="col-md-2">{this.props.user.name}</span>
                <span className="col-md-1">{this.props.user.balance}</span>
                <OptionalValue default={this.props.user.defaultParts}/>
            </div>
        );}
});

var OptionalValue = React.createClass({
    getInitialState: function() {
        return {checked:false, initvalue: 1.0,  value:1.0};
    },
    handleClick: function(event){
        this.setState({checked: !this.state.checked});
    },
    handleNumChange: function(event) {
        this.setState({value: event.target.value});
    },
    render: function() {
        var numCls = classNames({ 'hidden': !this.state.checked });
        var chkCls = classNames({ });
        return (
            <div className="fluid-container optval">
                <div className="col-md-1">
                    <input type="number" name='factor'
                        defaultValue={this.props.default}
                        onChange={this.handleNumChange}
                        className={numCls}></input>
                </div>
                <div className="col-md-1">
                    <input type="checkbox" checked={this.state.checked}
                        onClick={this.handleClick}
                        className={chkCls} ></input>
                </div>
            </div>
        );}
});

var dummy = [ {id:1, name: "Intey", defaultParts: 0.5, balance: -123.54}, {id:2, name: "andreyk", defaultParts:1.0, balance: 10023.0} ];
ReactDOM.render(
    <UserTable users={dummy}/>,
    document.getElementById('content')
);
