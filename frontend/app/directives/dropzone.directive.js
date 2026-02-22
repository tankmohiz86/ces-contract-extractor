angular.module('cesApp')
  .directive('ceDropZone', ['$parse', function($parse) {
    return {
      restrict: 'A',
      link: function(scope, element, attrs) {
        var onFileDrop = $parse(attrs.onFileDrop);

        element[0].addEventListener('dragover', function(e) {
          e.preventDefault();
          e.stopPropagation();
          scope.$apply(function() { scope.vm.isDragging = true; });
        });

        element[0].addEventListener('dragleave', function(e) {
          e.preventDefault();
          e.stopPropagation();
          scope.$apply(function() { scope.vm.isDragging = false; });
        });

        element[0].addEventListener('drop', function(e) {
          e.preventDefault();
          e.stopPropagation();
          scope.$apply(function() {
            scope.vm.isDragging = false;
            var files = e.dataTransfer.files;
            if (files && files.length > 0) {
              onFileDrop(scope, { file: files[0] });
            }
          });
        });
      }
    };
  }]);
