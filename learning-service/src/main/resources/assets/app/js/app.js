(function () {
// Declare app level module which depends on filters, and services
    angular.module('learning',
        ['ngResource', 'ngRoute', 'ui.bootstrap', 'ui.date'])
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/', {
                    templateUrl: 'views/home/home.html',
                    controller: 'HomeController'
                })
                .when('/websocket/', {
                    templateUrl: 'views/websocket/websocket.html'
                })
                .when('/websocket/adapter', {
                    templateUrl: 'views/websocket/websocket-adapter.html'
                })
                .when('/error/', {
                    templateUrl: 'error.html'
                })
                .otherwise({redirectTo: '/'});
        }]);
})();
