// Declare app level module which depends on filters, and services
angular.module('learning', ['ngResource', 'ngRoute', 'ui.bootstrap', 'ui.date'])
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/home/home.html',
        controller: 'HomeController'})
        .when('/error/', {
            templateUrl : 'error.html'
        })
      .otherwise({redirectTo: '/'});
  }]);
