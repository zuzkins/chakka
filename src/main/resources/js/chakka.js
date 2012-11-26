'use strict';

var chakka = angular.module('chakka', []).config(
  ['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {templateUrl: 'views/join.html',
                            controller: 'JoinCtrl'})}]
);

chakka.controller('JoinCtrl', function($scope) {
  $scope.join = function() {
    alert('Hello: ' + $scope.username);
  }
});
