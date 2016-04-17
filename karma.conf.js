// Karma configuration
module.exports = function(config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '',

        // testing framework to use (jasmine/mocha/qunit/...)
        frameworks: ['jasmine'],

        plugins: [
            'karma-jasmine',
            'karma-coverage',
            'karma-chrome-launcher',
            'karma-ng-json2js-preprocessor',
            'karma-ng-html2js-preprocessor'
        ],

        // list of files / patterns to exclude
        exclude: [],

        // web server port
        port: 8765,

        // level of logging
        // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,

        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - PhantomJS
        // - IE (only Windows)
        browsers: ['Chrome'],


        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: true,

        // karma-coverage reports
        preprocessors: {
            'learning-service/src/main/resources/assets/app/js/app.js': ['coverage'],
            'learning-service/src/main/resources/app/js/**/*.js': ['coverage'],
            'learning-service/src/main/resources/assets/app/views/**/*.html': ['ng-html2js']
        },
        reporters: ['progress', 'coverage'],
        coverageReporter: {
            type : 'html',
            dir : 'learning-service/target/karma/'
        },

        ngHtml2JsPreprocessor: {
            // strip this from the file path
            stripPrefix: 'learning-service/src/resources/assets/app/'
        }
    });
};
