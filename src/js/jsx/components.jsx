var React = require('react');
var ReactDOM = require('react-dom');

var CheckItem = React.createClass({
    render: function() {
        return (
            <div className="item">
                <span>{this.props.name}</span>
                <span>{this.props.price}</span>
            </div>
        );
    }
});

var Check = React.createClass({
    render: function() {
        var checkItems = this.props.data.map(function(itm) {
            return (
                <CheckItem name={itm.name} price={itm.price} key={itm.id}>
                </CheckItem>
            );
        });
        return (
            <div className="check">
                {checkItems}
            </div>
        );
    }
});

var dummy = [
    {name:"Pivas", price:290, id:2},
    {name:"Pivas", price:290, id:1},
    {name:"Vinco", price:590, id:3}
];

ReactDOM.render(
    <Check data={dummy}/>,
    document.getElementById('check')
);
