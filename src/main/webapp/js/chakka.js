'use strict';

var chakka = angular.module('chakka', []).config(
  ['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {templateUrl: 'views/join.html',
                            controller: 'JoinCtrl'}),
  $routeProvider.when('/chat', {templateUrl: 'views/chatroom.html',
                            controller: 'ChatCtrl'})}]
).factory('CurUser', function() {
  return { username: null, chatroom: null};
}).factory('ChatRoomSocket', function() {
  return ChatRoomSocket;
});

chakka.controller('JoinCtrl', function($scope, $location, CurUser) {
  $scope.user = CurUser;

  $scope.join = function() {
    $location.path("/chat");
  }
});

chakka.controller('ChatCtrl', function($scope, CurUser, ChatRoomSocket) {

  var s = new ChatRoomSocket(CurUser.chatroom, CurUser.username);

  $scope.sendMessage = function() {
    if ($scope.msg) {
      s.sendMessage({name: 'msg', body: {msg: $scope.msg}});
      $scope.msg = null;
    }
  };

  $scope.msgs = [];
  s.setOnMessageListener(function(evt) {
    var data = JSON.parse(evt.data);
    $scope.$apply(function() {
      $scope.msgs.push(data.body);
    });
  });

});

var ChatRoomSocket = function(chatroom, username) {
    this.ws = new WebSocket('ws://' + window.location.host + '/chat/join/' + chatroom + '!' + username);
};
ChatRoomSocket.prototype.setOnMessageListener = function(listener) {
  console.log('listener set');
  this.ws.onmessage = listener;
};
ChatRoomSocket.prototype.sendMessage = function(data) {
  this.ws.send(JSON.stringify(data));
};



