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
var app = angular.module(appName, [ "ngAnimate", "ngRoute", "ngSanitize", "pascalprecht.translate", "angularModalService" ]);

app.run(function($animate) {
	$animate.enabled(true);
});

app.config(function($translateProvider, $routeProvider, $locationProvider) {
	$translateProvider.useStaticFilesLoader({
		prefix : "/assets/i18n/i18n-",
		suffix : ".json"
	});

	$translateProvider.preferredLanguage("de_DE");
	$translateProvider.fallbackLanguage("en_US");

	$routeProvider.when("/", {
		templateUrl : "/assets/templates/dashboard.html",
		name : "dashboard",
		icon : "fa fa-tachometer"
	}).when("/packages", {
		templateUrl : "/assets/templates/packages.html",
		name : "packages",
		icon : "fa fa-cubes"
	}).when("/users", {
		templateUrl : "/assets/templates/users.html",
		name : "users",
		icon : "fa fa-user"
	}).when("/groups", {
		templateUrl : "/assets/templates/groups.html",
		name : "groups",
		icon : "fa fa-users"
	}).otherwise({
		redirectTo : '/'
	});
});

app.factory('routeNavigation', function($route, $location) {
	var routes = [];
	angular.forEach($route.routes, function(route, path) {
		if (route.name) {
			routes.push({
				path : path,
				name : route.name,
				icon : route.icon
			});
		}
	});
	return {
		routes : routes,
		activeRoute : function(route) {
			return route.path === $location.path();
		}
	};
});

app.directive('navigation', function(routeNavigation) {
	return {
		restrict : "E",
		replace : true,
		templateUrl : "/assets/templates/navigation.html",
		controller : function($scope) {
			$scope.routes = routeNavigation.routes;
			$scope.activeRoute = routeNavigation.activeRoute;
		}
	};
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

app.controller("dashboard", function($scope) {
});

app.controller("packages", function($scope, $log, $http, ModalService, asyncQueue) {
	$scope.packages = {};

	$scope.loadData = function() {
		asyncQueue.load([ "/manage/packages" ]).then(function(results) {
			results.forEach(function(result) {
				if (result.status === 200) {
					if (result.config.url.indexOf("/packages") != -1) {
						$scope.packages = result.data;
					}
				}
			});
		}, function(error) {
			$log.error(error);
		});
	};

	$scope.showPackageDialog = function(p) {
		ModalService.showModal({
			templateUrl : "/assets/templates/package-dialog.html",
			controller : "packageDialog",
			inputs : {
				p : angular.copy(p),
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(p) {
				if (p !== undefined) {
					$scope.updatePackage(p);
				}
			});
		});
	};

	$scope.updatePackage = function(p) {
		$http.post("/manage/packages/update", p).then(function() {
			for ( var i in $scope.packages["package"]) {
				if ($scope.packages["package"][i].id == p.id) {
					$scope.packages["package"][i] = p;
					return;
				}
			}
			$scope.packages["package"].push(p);
		}, function(e) {
			$log.error(e);
		});
	}

	$scope.loadData();
});

app.controller('packageDialog', function($scope, p, close) {
	$scope.headline = 'package.headline.' + (p === undefined ? 'create' : 'edit');

	if (p["function"] !== undefined) {
		var func = p["function"];
		func.value = "function " + func.name + "(" + (func.params || "") + ") {\n" + func.value + "\n}";
	}
	$scope["package"] = p;

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		if ($scope["package"]["function"] !== undefined) {
			var regexp = /function\s([^\(]+)\(([^\)]*)\)\s*{([^}]*)}/g;
			var match = regexp.exec($scope["package"]["function"].value);
			if (match) {
				$scope["package"]["function"].name = match[1].trim();
				$scope["package"]["function"].params = match[2].trim();
				$scope["package"]["function"].value = match[3].trim();
			}
		}

		close($scope["package"], 500);
	};

});

app.controller("users", function($scope, $log, $http, ModalService, asyncQueue) {
	$scope.users = {};
	$scope.groups = {};

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

	$scope.showUserDialog = function(user) {
		ModalService.showModal({
			templateUrl : "/assets/templates/user-dialog.html",
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
			$log.error(e);
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
			templateUrl : "/assets/templates/group-dialog.html",
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
			$scope.groups.group.push(group);
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