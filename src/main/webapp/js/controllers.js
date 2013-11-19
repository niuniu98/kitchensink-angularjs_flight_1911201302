/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function MembersCtrl($scope, $http, Members, Items,Bookings) {

    // Define a refresh function, that updates the data from the REST service
    $scope.refresh = function() {
        $scope.members = Members.query();
    };

    $scope.refreshItem = function() {
        $scope.items = Items.query();
    };
    $scope.refreshBook = function() {
        $scope.bookings = Bookings.query();
    };
    // Define a reset function, that clears the prototype newMember object, and
    // consequently, the form
    $scope.reset = function() {
        // clear input fields
        $scope.newMember = {};
    };
    
    $scope.resetItem = function() {
        // clear input fields
        $scope.newItem = {};
    };
    
    $scope.resetBooking = function() {
        // clear input fields
        $scope.newBooking = {};
    };

    // Define a register function, which adds the member using the REST service,
    // and displays any error messages
    $scope.register = function() {
        $scope.successMessages = '';
        $scope.errorMessages = '';
        $scope.errors = {};

        Members.save($scope.newMember, function(data) {

            // mark success on the registration form
            $scope.successMessages = [ 'Member Registered' ];

            // Update the list of members
            $scope.refresh();

            // Clear the form
            $scope.reset();
        }, function(result) {
            if ((result.status == 409) || (result.status == 400)) {
                $scope.errors = result.data;
            } else {
                $scope.errorMessages = [ 'Unknown  server error' ];
            }
            $scope.$apply();
        });

    };
    
    $scope.registerItem = function() {
        $scope.itemSuccessMessages = '';
        $scope.itemErrorMessages = '';
        $scope.errors = {};

        Items.save($scope.newItem, function(data) {

            // mark success on the registration form
            $scope.itemSuccessMessages = [ 'Item Registered' ];

            // Update the list of members
            $scope.refreshItem();

            // Clear the form
            $scope.resetItem();
        }, function(result) {
            if ((result.status == 409) || (result.status == 400)) {
                $scope.errors = result.data;
            } else {
                $scope.itemErrorMessages = [ 'Unknown  server error' ];
            }
            $scope.$apply();
        });

    };
    
    $scope.registerBooking = function() {
        $scope.successMessages = '';
        $scope.errorMessages = '';
        $scope.errors = {};

        Bookings.save($scope.newBooking, function(data) {

            // mark success on the registration form
            $scope.successMessages = [ 'Member Registered' ];

            // Update the list of members
            $scope.refreshItem();
            $scope.refreshBook();

            // Clear the form
            $scope.resetBook();
        }, function(result) {
            if ((result.status == 409) || (result.status == 400)) {
                $scope.errors = result.data;
            } else {
                $scope.errorMessages = [ 'Unknown  server error' ];
            }
            $scope.$apply();
        });

    };
    
    //Delete
    $scope.remove=function(booking){
    	//alert(member.id);
    	Bookings.remove({bookingId:booking.id} , function(data) {

            // mark success on the registration form
            $scope.successMessages = [ 'Member Registered' ];

            // Update the list of members
            $scope.refreshItem();
            $scope.refreshBook();

        }, function(result) {
            if ((result.status == 409) || (result.statuss == 400)) {
                $scope.errors = result.data;
            } else {
                $scope.errorMessages = [ 'Unknown  server error' ];
            }
            $scope.$apply();
        });
    };

    // Call the refresh() function, to populate the list of members
    $scope.refresh();
    $scope.refreshItem();
    $scope.refreshBook();

    // Initialize newMember here to prevent Angular from sending a request
    // without a proper Content-Type.
    $scope.reset();
    $scope.resetItem();
    $scope.resetBooking();

    // Set the default orderBy to the name property
    $scope.orderBy = 'id';
} 