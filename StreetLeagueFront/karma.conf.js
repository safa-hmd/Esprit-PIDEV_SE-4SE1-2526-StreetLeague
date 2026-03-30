module.exports = function (config) {

  // Détecte l'environnement : Jenkins (Linux) ou Windows local
  const isCI = process.env.CI || process.env.JENKINS_HOME;
  
  if (isCI) {
    // Jenkins Linux — utilise chromium installé
    process.env.CHROME_BIN = '/usr/bin/chromium';
  } else {
    // Windows local — utilise Avast Browser
    process.env.CHROME_BIN = 'C:\\Program Files\\AVAST Software\\Browser\\Application\\AvastBrowser.exe';
  }

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
      ChromeHeadlessNoSandbox: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox', '--disable-gpu']
      }
    },
    browsers: ['ChromeHeadlessNoSandbox'],
    singleRun: false,
    restartOnFileChange: true
  });
};