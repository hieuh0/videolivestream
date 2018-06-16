var express = require('express');
var app = express();
app.set('view engine', 'ejs');
app.use(express.static('public'));
var server = require("http").Server(app);
var io = require("socket.io")(server);
server.listen(3000);

// index page 
app.get('/', function(req, res) {
    res.render('index');
});

app.get('/live',(req,res)=>{
  res.render('demo');
});



const NodeMediaServer = require('node-media-server');

const config = {
  logType: 3,

  rtmp: {
    port: 1935,
    chunk_size: 60000,
    gop_cache: true,
    ping: 60,
    ping_timeout: 10
  },
  http: {
    port: 8000,
    allow_origin: '*'
  }
};

var nms = new NodeMediaServer(config)
nms.run();
nms.on('preConnect', (id, args) => {
  console.log('[NodeEvent on preConnect]', `id=${id} args=${JSON.stringify(args)}`);
  // let session = nms.getSession(id);
  // session.reject();
});

nms.on('postConnect', (id, args) => {
  console.log('[NodeEvent on postConnect]', `id=${id} args=${JSON.stringify(args)}`);
});

nms.on('doneConnect', (id, args) => {
  console.log('[NodeEvent on doneConnect]', `id=${id} args=${JSON.stringify(args)}`);
});

nms.on('prePublish', (id, StreamPath, args) => {
  console.log('[NodeEvent on prePublish]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
  // let session = nms.getSession(id);
  // session.reject();
});

nms.on('postPublish', (id, StreamPath, args) => {
  console.log('[NodeEvent on postPublish]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
});

nms.on('donePublish', (id, StreamPath, args) => {
  console.log('[NodeEvent on donePublish]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
});

nms.on('prePlay', (id, StreamPath, args) => {
  console.log('[NodeEvent on prePlay]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
  // let session = nms.getSession(id);
  // session.reject();
});

nms.on('postPlay', (id, StreamPath, args) => {
  console.log('[NodeEvent on postPlay]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
});

nms.on('donePlay', (id, StreamPath, args) => {
  console.log('[NodeEvent on donePlay]', `id=${id} StreamPath=${StreamPath} args=${JSON.stringify(args)}`);
});


io.on("connection", function(socket){
  console.log("Co nguoi vua ket noi, socket id: " + socket.id);

  // socket.on("client_gui_username", function(data){
  //   console.log("Co nguoi dang ki username la: " + data);
  //   if( mangUsersOnline.indexOf(data)>=0){
  //     socket.emit("server-send-dangki-thatbai", data);
  //   }else{
  //     mangUsersOnline.push(data);
  //     socket.Username = data;
  //     io.sockets.emit("server-send-dangki-thanhcong", {username:data, id:socket.id});
  //   }
  // });

  socket.on("client_gui_message", function(data){
    // io.sockets.emit("server_gui_message", {Username:socket.Username, msg:data});
    //socket.emit("server_gui_message", { msg:data});
    io.sockets.emit("server_gui_message", { msg:data});
  });

  // socket.on("user-chocgheo-socketid-khac", function(data){
  //   io.to(data).emit("server_xuly_chocgheo", socket.Username);
  // })

});