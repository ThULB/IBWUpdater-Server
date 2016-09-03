/*
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 3
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
"use strict";

var appName = "IBWUpdater";
var app = angular.module(appName, [ "ngAnimate", "ngSanitize", "pascalprecht.translate", "angularModalService" ]);

app.run(function($animate) {
	$animate.enabled(true);
});

app.config(function($translateProvider) {
	$translateProvider.useStaticFilesLoader({
		prefix : "/web/assets/i18n/i18n-",
		suffix : ".json"
	});

	$translateProvider.preferredLanguage("de_DE");
	$translateProvider.fallbackLanguage('en_US');
});

app.service("asyncQueue", function($http, $q) {
	this.load = function(urls) {
		var deferred = $q.defer();

		var queue = [];
		angular.forEach(urls, function(url) {
			queue.push($http.get(url));
		});

		$q.all(queue).then(function(results) {
			deferred.resolve(results);
		}, function(errors) {
			deferred.reject(errors);
		}, function(updates) {
			deferred.update(updates);
		});
		return deferred.promise;
	};
});

app.controller("users", function($scope, $http, ModalService, asyncQueue) {
	$scope.users = {};
	$scope.groups = {};

	$scope.loadData = function() {
		asyncQueue.load([ "/manage/users", "/manage/groups" ]).then(function(results) {
			results.forEach(function(result) {
				if (result.status === 200) {
					if (result.config.url.indexOf("/users") != -1) {
						angular.merge($scope.users, result.data);
					} else if (result.config.url.indexOf("groups") != -1) {
						angular.merge($scope.groups, result.data);
					}
				}
			});
		}, function(error) {
			console.error(error);
		});
	};

	$scope.showUserDialog = function(user) {
		ModalService.showModal({
			templateUrl : "/web/assets/templates/user-dialog.html",
			controller : "userDialog",
			inputs : {
				user : angular.copy(user),
				groups : $scope.groups
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(user) {
				if (user !== undefined) {
					$scope.updateUser(user);
				}
			});
		});
	};

	$scope.updateUser = function(user) {
		$http.post("/manage/users/update", user).then(function() {
			for ( var i in $scope.users.user) {
				if ($scope.users.user[i].id == user.id) {
					$scope.users.user[i] = user;
					return;
				}
			}
			$scope.users.user.push(user);
		}, function(e) {
			console.error(e);
		});
	}

	$scope.loadData();
});

app.controller('userDialog', function($scope, user, groups, close) {

	$scope.user = user;
	$scope.groups = groups;
	$scope.headline = 'user.headline.' + (user === undefined ? 'create' : 'edit');

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		close($scope.user, 500);
	};

});

app.controller("groups", function($scope, $http, $log, ModalService, asyncQueue) {
	$scope.groups = {};
	$scope.users = {};

	$scope.loadData = function() {
		asyncQueue.load([ "/manage/users", "/manage/groups" ]).then(function(results) {
			results.forEach(function(result) {
				if (result.status === 200) {
					if (result.config.url.indexOf("/users") != -1) {
						$scope.users = result.data;
					} else if (result.config.url.indexOf("groups") != -1) {
						$scope.groups = result.data;
					}
				}
			});
		}, function(error) {
			$log.error(error);
		});
	};

	$scope.showGroupDialog = function(group) {
		ModalService.showModal({
			templateUrl : "/web/assets/templates/group-dialog.html",
			controller : "groupDialog",
			inputs : {
				group : group,
				users : $scope.users
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(group) {
				if (group !== undefined) {
					$scope.updateGroup(group);
				}
			});
		});
	};

	$scope.updateGroup = function(group) {
		$http.post("/manage/groups/update", group).then(function() {
			for ( var i in $scope.groups.group) {
				if ($scope.groups.group[i].id == group.id) {
					$scope.groups.group[i] = group;
					return;
				}
			}
			$scope.groups.group.push(user);
		}, function(e) {
			$log.error(e);
		});
	}

	$scope.loadData();
});

app.controller('groupDialog', function($scope, group, users, close) {

	$scope.group = group;
	$scope.users = users;
	$scope.headline = 'group.headline.' + (group === undefined ? 'create' : 'edit');

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		close($scope.group, 500);
	};

});