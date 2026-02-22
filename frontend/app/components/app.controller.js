angular.module('cesApp')
  .controller('AppController', ['$scope', '$timeout', 'ContractService',
    function($scope, $timeout, ContractService) {
      var vm = this;

      vm.selectedFile = null;
      vm.contracts    = [];
      vm.loading      = false;
      vm.error        = null;
      vm.stage        = 0;
      vm.isDragging   = false;

      // ── Load existing contracts on startup ──────────────────────
      ContractService.getAll()
        .then(function(res) { vm.contracts = res.data; })
        .catch(function() { /* silent — backend may not be up yet */ });

      // ── File selection via input ─────────────────────────────────
      vm.onFileSelected = function(input) {
        if (input.files && input.files[0]) {
          vm.selectedFile = input.files[0];
          vm.error = null;
          $scope.$apply();
        }
      };

      // ── File drop from directive ─────────────────────────────────
      vm.onFileDrop = function(file) {
        if (file.type !== 'application/pdf') {
          vm.error = 'Only PDF files are accepted.';
          return;
        }
        vm.selectedFile = file;
        vm.error = null;
      };

      // ── Trigger hidden file input ────────────────────────────────
      vm.triggerFileInput = function() {
        if (!vm.selectedFile) {
          document.getElementById('fileInput').click();
        }
      };

      // ── Clear selected file ──────────────────────────────────────
      vm.clearFile = function($event) {
        $event.stopPropagation();
        vm.selectedFile = null;
        document.getElementById('fileInput').value = '';
        vm.error = null;
      };

      // ── Format file size ─────────────────────────────────────────
      vm.formatFileSize = function(bytes) {
        if (!bytes) return '';
        if (bytes < 1024) return bytes + ' B';
        if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
        return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
      };

      // ── Main extract pipeline ────────────────────────────────────
      vm.extract = function() {
        if (!vm.selectedFile || vm.loading) return;

        vm.loading = true;
        vm.error   = null;
        vm.stage   = 1;

        // Simulate stage progression for UX feedback
        $timeout(function() { vm.stage = 2; }, 600);
        $timeout(function() { vm.stage = 3; }, 2400);
        $timeout(function() { vm.stage = 4; }, 3200);

        ContractService.upload(vm.selectedFile)
          .then(function(res) {
            vm.contracts.unshift(res.data);
            vm.selectedFile = null;
            document.getElementById('fileInput').value = '';
            vm.stage = 5; // all done
          })
          .catch(function(err) {
            var msg = (err.data && err.data.error) ? err.data.error : 'Extraction failed. Check backend logs.';
            vm.error = msg;
          })
          .finally(function() {
            vm.loading = false;
            vm.stage   = 0;
          });
      };

      // ── Delete contract ──────────────────────────────────────────
      vm.deleteContract = function(id) {
        ContractService.deleteContract(id)
          .then(function() {
            vm.contracts = vm.contracts.filter(function(c) { return c.id !== id; });
          })
          .catch(function() {
            vm.error = 'Failed to delete record.';
          });
      };
    }
  ]);
