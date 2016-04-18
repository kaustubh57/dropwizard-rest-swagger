var gulp = require('gulp'),
    KarmaTestServer = require('karma').Server;

var testSources = [
    // sequence of app lib is same as `index.html`
    'learning-service/src/main/resources/assets/app/lib/jquery/dist/jquery.js',
    'learning-service/src/main/resources/assets/app/lib/jquery-ui/jquery-ui.js',
    'learning-service/src/main/resources/assets/app/lib/lodash/dist/lodash.js',
    'learning-service/src/main/resources/assets/app/lib/angular/angular.js',
    'learning-service/src/main/resources/assets/app/lib/angular-resource/angular-resource.js',
    'learning-service/src/main/resources/assets/app/lib/angular-route/angular-route.js',
    'learning-service/src/main/resources/assets/app/lib/angular-bootstrap/ui-bootstrap-tpls.js',
    'learning-service/src/main/resources/assets/app/lib/angular-ui-date/src/date.js',
    'learning-service/src/main/resources/assets/app/lib/angular-mocks/angular-mocks.js',

    'learning-service/src/main/resources/assets/app/js/app.js',
    'learning-service/src/main/resources/assets/app/js/home/home-controller.js',
    'learning-service/src/main/resources/assets/test/spec/**/*.js',
    'learning-service/src/main/resources/assets/app/views/**/*.html'
];

// gulp task starts

gulp.task('karma-test', function(done) {
    KarmaTestServer.start({
        configFile: __dirname + '/karma.conf.js',
        files: testSources
    }, function() {
        done();
    });
});

gulp.task('copy-report', function(){
    gulp.src('learning-service/src/main/resources/assets/test/report/Learning-UI-test-report.xml')
        .pipe(gulp.dest('learning-service/src/main/resources/assets/test/report/', {overwrite: true}));
});
