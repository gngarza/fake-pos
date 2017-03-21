
var fakePosModule = angular.module('fake-pos', ['ngMaterial'])

fakePosModule.controller('orderEntryScreen', function($scope, $http) {

    $scope.$on('itemSelected', function(event, message) {
        $scope.$broadcast('addToOrder', message);
    });

});


fakePosModule.controller('availableItems', function($scope, $http) {

    // Load available items
    $http.get('/api/items/getAllItems').then(function(response) {
        $scope.availableItems = response.data;
    })

    $scope.itemSelected = function(itemName) {
        $scope.$emit('itemSelected', itemName);
    };

});

fakePosModule.controller('orderDetails', function($scope, $http, $mdDialog) {

    $scope.selectedLineItem = null;

    var pendingItem = null;

    $scope.$on('addToOrder', function(event, item) {
        if($scope.order.tenderRecord == null) {
            addToOrder(item);
        } else {
            // I don't like this, but creating new orders is going outside the spec
            // and it at least lets us make multiple orders without refreshing the
            // page.
            pendingItem = item;
            createNewOrder();
        }
    });

    $scope.displayOrderId = displayOrderId();

    function displayOrderId() {
        if($scope.order == null || $scope.order.orderNumber == -1) {
            return "New Order";
        } else {
            return "Order #" + $scope.order.displayOrderNumber;
        }
    }

    function createNewOrder() {
        return $http.post('/api/orders/createNew').then(function(response) {
            $scope.order = response.data;
            $scope.selectedLineItem = null;
            $scope.displayOrderId = displayOrderId();
            if(pendingItem != null) {
                addToOrder(pendingItem);
                pendingItem = null;
            }
        });
    }

    function addToOrder(item) {
        $http.post('/api/orders/addItem', {itemName: item.name}).then(function(response) {
            $scope.order = response.data;
            $scope.selectedLineItem = null;
        });
    }

    $scope.lineItemSelected = function(orderLineItem) {
        if($scope.selectedLineItem != null) {
            $scope.selectedLineItem.selected = false;
        }
        orderLineItem.selected = true;
        $scope.selectedLineItem = orderLineItem;
    }

    $scope.voidItem = function() {
        $http
            .post('/api/orders/voidItem', {itemName: $scope.selectedLineItem.item.name})
            .then(function(response) {
                $scope.order = response.data;
                $scope.selectedLineItem = null;
            });
    }

    $scope.showTenderDialog = function() {

        var promise = $mdDialog.show({
            locals: {order: $scope.order},
            title: 'Tender Payment',
            template: tenderDialogTemplate,
            controller: 'tenderDialog'
        });

        promise.then(
            function(order) {
                $scope.order = order;
                $scope.displayOrderId = displayOrderId();
            },
            function(failure) {
                console.log('Internal error', failure);
            });
    }

    createNewOrder();

});

var tenderDialogTemplate =
   '<md-dialog class="tender-dialog">' +
   '  <md-dialog-content layout="column">' +
   '     <h2 flex="none">Tender Payment</h2>' +
   '     <div layout="column" class="amounts">' +
   '       <div class="form-error">{{tenderError}}</div>' +
   '       <div class="form-row" layout="row">' +
   '         <span class="label" flex="50">Amount Due:</span> ' +
   '         <span flex="50" class="price">{{order.displayGrandTotal}}</span>' +
   '       </div>' +
   '       <div class="form-row" layout="row">' +
   '         <span class="label" flex="50">Amount Tendered:</span> ' +
   '         <span flex="50">' +
   '           <input md-autofocus type="text" size="8" ' +
   '                  ng-model="amountTenderedInput" ng-change="tenderAmountUpdated()">' +
   '         </span>' +
   '       </div>' +
   '       <div class="form-row" layout="row">' +
   '         <span class="label" flex="50">Change Due:</span> ' +
   '         <span flex="50" class="price">{{changeDue}}</span>' +
   '       </div>' +
   '  </md-dialog-content>' +
   '  <md-dialog-actions>' +
   '    <div layout="row" class="tender-buttons">' +
   '      <button flex="none" ng-click="closeDialog()" class="md-primary">' +
   '        Cancel' +
   '      </button>' +
   '      <span flex="grow"></span> ' +
   '      <button flex="none" ' +
   '              ng-disabled="!tenderedAmountValid" ' +
   '              ng-click="completeOrder()">' +
   '        Tender' +
   '      </button>' +
   '    </div>' +
   '  </md-dialog-actions>' +
   '</md-dialog>'

fakePosModule.controller('tenderDialog', function($scope, $http, $mdDialog, order) {

    $scope.order = order;

    $scope.changeDue = "0.00";

    $scope.amountTenderedInput = "";

    $scope.tenderError = "";

    $scope.tenderedAmountValid = false;

    function handleTenderError(resp) {
        $scope.tenderError = resp.errorMessage;
        $scope.tenderedAmountValid = false;
        $scope.changeDue = "0.00";
    }

    $scope.tenderAmountUpdated = function() {
        if($scope.amountTenderedInput == "") {
            $scope.tenderError = "";
            return;
        }

        $http.get('/api/orders/calcChangeDue',
                  {params: {amountTendered : $scope.amountTenderedInput}})
             .then(function(response) {

            if(!response.data.valid) {
                handleTenderError(response.data);
            } else {
                $scope.tenderError = "";
                $scope.changeDue = response.data.displayChangeDue;
                $scope.tenderedAmountValid = true;
            }
        });
    }

    $scope.closeDialog = function() {
        $mdDialog.cancel();
    };

    $scope.completeOrder = function() {
        $http.post('/api/orders/completeOrder',
                   {amountTenderedInput: $scope.amountTenderedInput})
             .then(function(response) {

            if(!response.data.valid) {
                handleTenderError(response.data);
            } else {
                $mdDialog.hide(response.data.order);
            }
        });
    };

});
