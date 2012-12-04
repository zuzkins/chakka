'use strict';

var chakka = angular.module('chakka', []).config(
  ['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {templateUrl: 'views/join.html',
                            controller: 'JoinCtrl'})
                .when('/chat', {templateUrl: 'views/chatroom.html',
                            controller: 'ChatCtrl'})
                .otherwise({redirectTo: '/'})}]
).run(function($rootScope, $location, CurUser) {

    // register listener to watch route changes
    $rootScope.$on( "$routeChangeStart", function(event, next, current) {

      if (!CurUser.username || !CurUser.chatroom) {
        // no logged user, we should be going to #login
        if ( next.templateUrl == "views/join.html" ) {
          // already going to #login, no redirect needed
        } else {
          // not going to #login, we should redirect now
          $location.path( "/" );
        }
      }         
    })
  }).factory('CurUser', function() {
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

chakka.controller('ChatCtrl', function($scope, $location, CurUser, ChatRoomSocket) {

  var s = new ChatRoomSocket(CurUser.chatroom, CurUser.username);

  $scope.chatroom = CurUser.chatroom;

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
      dispatchMsg($scope, data);
    });
  });

  s.setOnSocketClosedListener(function(){
    console.log("closing");
    alert('The connection was unexpectedly closed');;
  });

  $scope.logout = function() {
    s.close();
    $location.path("/");
  }
});

function dispatchMsg($scope, msg) {
  if (!msg || !msg.name) {
    console.log('invalid message received through websocket:', msg);
    return;
  }
  var name = msg.name;

  if (name === 'msg') {
    $scope.msgs.push(msg.body);
  } else if (name === 'userList') {
    $scope.users = msg.body.usernames;
  } else {
    console.log('Unknown command: ' + name);
  }
}

var ChatRoomSocket = function(chatroom, username) {
    this.ws = new WebSocket('ws://' + window.location.host + '/chat/join/' + chatroom + '!' + username);
    var self = this;
    this.ws.onopen = function(evt) {
      self.sendMessage({name: 'listUsers'});
    }
};
ChatRoomSocket.prototype.setOnMessageListener = function(listener) {
  this.ws.onmessage = listener;
};
ChatRoomSocket.prototype.sendMessage = function(data) {
  this.ws.send(JSON.stringify(data));
};
ChatRoomSocket.prototype.setOnSocketClosedListener = function(listener) {
  this.ws.onclose = listener;
}
ChatRoomSocket.prototype.close = function() {
  this.ws.onclose = null;
  this.ws.close();
}



