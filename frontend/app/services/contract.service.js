angular.module('cesApp')
  .service('ContractService', ['$http', '$window', function($http, $window) {

    // Backend URL — in Docker the Nginx proxy handles /api/* -> backend
    // For local non-Docker dev you can override via window.BACKEND_URL
    var BASE = ($window.BACKEND_URL || '') + '/api/contracts';

    this.upload = function(file) {
      var formData = new FormData();
      formData.append('file', file);

      return $http.post(BASE + '/upload', formData, {
        transformRequest: angular.identity,
        headers: { 'Content-Type': undefined }
      });
    };

    this.getAll = function() {
      return $http.get(BASE);
    };

    this.deleteContract = function(id) {
      return $http.delete(BASE + '/' + id);
    };

    this.healthCheck = function() {
      return $http.get(BASE + '/health');
    };

  }]);
