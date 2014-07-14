'use strict';

module.exports = function (grunt) {

  require('load-grunt-tasks')(grunt);

  grunt.initConfig({
    watch: {
      js: {
        files: ['build/cookoo.js'],
        options: {
          livereload: true,
        }
      },
    },

    concurrent: {
      options: {
        logConcurrentOutput: true
      },
      build_and_watch: [
        'cljs_test',
        'watch:js',
      ],
    },

    shell: {
      options: {
        stdout: true
      },
      cljs_dev: {
        command: 'lein do cljsbuild clean, cljsbuild auto dev'
      },
      cljs_test: {
        command: 'lein do cljsbuild clean, cljsbuild auto test'
      },
      cljs_release: {
        command: 'lein do cljsbuild clean, cljsbuild auto release'
      },
    }
  });

  grunt.registerTask('cljs_dev', ['shell:cljs_dev']);
  grunt.registerTask('cljs_test', ['shell:cljs_test']);
  grunt.registerTask('cljs_release', ['shell:cljs_release']);
  grunt.registerTask('build_and_watch', ['concurrent:build_and_watch']);
  grunt.registerTask('default', ['build_and_watch']);
};
