process.env.CHROME_BIN = process.env.CHROME_BIN || 
  'C:\\Program Files\\AVAST Software\\Browser\\Application\\AvastBrowser.exe';module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      clearContext: false
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' }
      ]
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
customLaunchers: {
  AvastBrowser: {
    base: 'Chrome',
    executablePath: 'C:\\Program Files\\AVAST Software\\Browser\\Application\\AvastBrowser.exe',
    flags: ['--no-sandbox', '--disable-gpu', '--headless']
  }
},
browsers: ['AvastBrowser'],
    browsers: ['AvastBrowser'],
    singleRun: false,
    restartOnFileChange: true
  });
};