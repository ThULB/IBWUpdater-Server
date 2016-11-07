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
		redirectTo : "/"
	});
});

app.factory("routeNavigation", function($route, $location) {
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

app.directive("navigation", function(routeNavigation) {
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

app.controller("alertCtrl", function($rootScope, $scope, $translate) {
	$scope.alertObj = {};

	$rootScope.$on("alertEvent", function(event, type, obj) {
		if (obj === null) {
			return;
		}

		$scope.alertObj.type = type;
		$scope.alertObj.show = true;
		if (typeof obj === "string") {
			$scope.alertObj.headline = $translate.instant("alert.type." + type);
			$scope.alertObj.message = obj;
		} else {
			$scope.alertObj.headline = obj.localizedHeadline ? $translate.instant(obj.localizedHeadline) : undefined ||
					$translate.instant("alert.type." + type);
			$scope.alertObj.message = obj.localizedMessage ? $translate.instant(obj.localizedMessage) : undefined || obj.message;
			$scope.alertObj.stackTrace = obj.stackTrace;
		}
	});

	$scope.clear = function() {
		$scope.alertObj.show = false;
	};
});

app.controller("dashboardCtrl", function($rootScope, $scope) {
});

app.controller("deleteConfirmDialogCtrl", function($scope, options, close) {
	$scope.options = options;

	$scope.close = function(result) {
		close(result, 500);
	};
});

app.controller("packagesCtrl", function($rootScope, $scope, $log, $http, $translate, ModalService, asyncQueue) {
	$scope.packages = {};

	$scope.loadData = function() {
		asyncQueue.load([ "/manage/packages" ]).then(function(results) {
			results.forEach(function(result) {
				if (result.status === 200) {
					if (result.config.url.indexOf("/packages") !== -1) {
						$scope.packages = result.data;
					}
				}
			});
		}, function(error) {
			$rootScope.$emit("alertEvent", "error", error.data);
			$log.error(error);
		});
	};

	$scope.showPackageDialog = function(p) {
		ModalService.showModal({
			templateUrl : "/assets/templates/package-dialog.html",
			controller : "packageDialogCtrl",
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

	$scope.showPackageDeleteDialog = function(p) {
		ModalService.showModal({
			templateUrl : "/assets/templates/delete-confirm-dialog.html",
			controller : "deleteConfirmDialogCtrl",
			inputs : {
				options : {
					headline : $translate.instant("package.headline.delete"),
					message : $translate.instant("package.message.delete").replace("{0}", p.name),
					object : angular.copy(p)
				},
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(p) {
				if (p !== undefined) {
					$scope.deletePackage(p);
				}
			});
		});
	};

	$scope.showPermissionDialog = function(p) {
		ModalService.showModal({
			templateUrl : "/assets/templates/permission-dialog.html",
			controller : "permissionDialogCtrl",
			inputs : {
				p : angular.copy(p),
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(permissions) {
				if (permissions !== undefined) {
					$scope.updatePermissions(permissions);
				}
			});
		});
	};

	$scope.updatePackage = function(p) {
		function toFormData(p, f) {
			var fd = new FormData();
			p.file = undefined;
			fd.append("package", new Blob([ angular.toJson(p) ], {
				type : "application/json"
			}));
			fd.append("file", f, f.name);
			return fd;
		}

		if (p.id === undefined) {
			$http.post("/manage/packages/add", p.file !== undefined ? toFormData(angular.copy(p), p.file) : p, p.file !== undefined ? {
				transformRequest : angular.identity,
				headers : {
					"Content-Type" : undefined
				}
			} : {}).then(function(result) {
				if (result.status === 200) {
					$scope.packages["package"].push(result.data);
				}
			}, function(e) {
				$rootScope.$emit("alertEvent", "error", e.data);
				$log.error(e);
			});
		} else {
			$http.post("/manage/packages/update", p.file !== undefined ? toFormData(angular.copy(p), p.file) : p, p.file !== undefined ? {
				transformRequest : angular.identity,
				headers : {
					"Content-Type" : undefined
				}
			} : {}).then(function(result) {
				if (result.status == 200) {
					p = result.data;
					for ( var i in $scope.packages["package"]) {
						if ($scope.packages["package"][i].id === p.id) {
							$scope.packages["package"][i] = p;
							return;
						}
					}
				}
			}, function(e) {
				$rootScope.$emit("alertEvent", "error", e.data);
				$log.error(e);
			});
		}
	};

	$scope.deletePackage = function(p) {
		$http.post("/manage/packages/delete", p).then(function(result) {
			if (result.status == 200) {
				$scope.loadData();
			}
		}, function(e) {
			$rootScope.$emit("alertEvent", "error", e.data);
			$log.error(e);
		});
	};

	$scope.updatePermissions = function(permissions) {
		$http.post("/manage/permissions/update", permissions).then(function(result) {
			if (result.status == 200) {
				$scope.loadData();
			}
		}, function(e) {
			$rootScope.$emit("alertEvent", "error", e.data);
			$log.error(e);
		});
	};

	$scope.loadData();
});

app.controller("packageDialogCtrl", function($scope, $element, p, close) {
	$scope.headline = "package.headline." + (p === undefined ? "create" : "edit");

	if (p !== undefined && p["function"] !== undefined) {
		var func = p["function"];
		func.value = "function " + func.name + "(" + (func.params || "") + ") {\n" + func.value + "\n}";
	}
	$scope["package"] = p;

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		if ($scope["package"].type === "common") {
			$scope["package"].file = $($element).find("#file")[0].files[0];
		}

		if ($scope["package"]["function"] !== undefined) {
			var regexp = /function\s([^\(]+)\(([^\)]*)\)\s*{([\w\W]*)}/g;
			var match = regexp.exec($scope["package"]["function"].value);
			if (match) {
				$scope["package"]["function"] = {};
				$scope["package"]["function"].name = match[1].trim();
				$scope["package"]["function"].params = match[2].trim();
				$scope["package"]["function"].value = match[3].trim();
			}
		}

		close($scope["package"], 500);
	};

});

app.controller("permissionDialogCtrl", function($scope, $log, $http, asyncQueue, p, close) {
	$scope["package"] = p;
	$scope.permissions = {};
	$scope.users = {};
	$scope.groups = {};

	$scope.loadData = function() {
		asyncQueue.load([ "/manage/permissions/" + p.id, "/manage/users", "/manage/groups" ]).then(function(results) {
			results.forEach(function(result) {
				if (result.status === 200) {
					if (result.config.url.indexOf("permissions") !== -1) {
						$scope.permissions = result.data;
						if ($scope.permissions && $scope.permissions.permission && $scope.permissions.permission.length === 0) {
							$scope.permissions.permission.push({});
						}
					} else if (result.config.url.indexOf("users") !== -1) {
						$scope.users = result.data;
					} else if (result.config.url.indexOf("groups") !== -1) {
						$scope.groups = result.data;
					}
				}
			});
		}, function(error) {
			$log.error(error);
		});
	};

	$scope.getSources = function(permission) {
		return permission.sourceType === "g" ? $scope.groups.group : permission.sourceType === "u" ? $scope.users.user : undefined;
	};

	$scope.deletePermission = function(permission) {
		$http.post("/manage/permissions/delete", permission).then(function(result) {
			if (result.status === 200) {
				$scope.loadData();
			}
		}, function(e) {
			$log.error(e);
		});
	};

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		var permissions = [];
		for (var i = 0; i < $scope.permissions.permission.length; i++) {
			var p = $scope.permissions.permission[i];
			if (p.sourceType && p.sourceId && p.sourceId !== 0 && p.packageId && p.action) {
				permissions.push(p);
			}
		}

		close({
			permission : permissions
		}, 500);
	};

	$scope.loadData();
});

app.controller("usersCtrl", function($rootScope, $scope, $log, $http, $translate, ModalService, asyncQueue) {
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
			$rootScope.$emit("alertEvent", "error", error.data);
			$log.error(error);
		});
	};

	$scope.showUserDialog = function(user) {
		ModalService.showModal({
			templateUrl : "/assets/templates/user-dialog.html",
			controller : "userDialogCtrl",
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

	$scope.showUserDeleteDialog = function(user) {
		ModalService.showModal({
			templateUrl : "/assets/templates/delete-confirm-dialog.html",
			controller : "deleteConfirmDialogCtrl",
			inputs : {
				options : {
					headline : $translate.instant("user.headline.delete"),
					message : $translate.instant("user.message.delete").replace("{0}", user.name),
					object : angular.copy(user)
				},
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(user) {
				if (user !== undefined) {
					$scope.deleteUser(user);
				}
			});
		});
	};

	$scope.updateUser = function(user) {
		if (user.id === undefined) {
			$http.post("/manage/users/add", user).then(function(result) {
				if (result.status == 200) {
					$scope.users.user.push(result.data);
				}
			}, function(e) {
				$rootScope.$emit("alertEvent", "error", e.data);
				$log.error(e);
			});
		} else {
			$http.post("/manage/users/update", user).then(function(result) {
				if (result.status == 200) {
					user = result.data;
					for ( var i in $scope.users.user) {
						if ($scope.users.user[i].id === user.id) {
							$scope.users.user[i] = user;
							return;
						}
					}
				}
			}, function(e) {
				$rootScope.$emit("alertEvent", "error", e.data);
				$log.error(e);
			});
		}
	};

	$scope.deleteUser = function(user) {
		$http.post("/manage/users/delete", user).then(function(result) {
			if (result.status == 200) {
				$scope.loadData();
			}
		}, function(e) {
			$rootScope.$emit("alertEvent", "error", e.data);
			$log.error(e);
		});
	};

	$scope.loadData();
});

app.controller("userDialogCtrl", function($scope, user, groups, close) {
	$scope.user = user;
	$scope.groups = groups;
	$scope.headline = "user.headline." + (user === undefined ? "create" : "edit");

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		close($scope.user, 500);
	};

});

app.controller("groupsCtrl", function($rootScope, $scope, $http, $log, $translate, ModalService, asyncQueue) {
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
			$rootScope.$emit("alertEvent", "error", error.data);
			$log.error(error);
		});
	};

	$scope.showGroupDialog = function(group) {
		ModalService.showModal({
			templateUrl : "/assets/templates/group-dialog.html",
			controller : "groupDialogCtrl",
			inputs : {
				group : angular.copy(group),
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

	$scope.showGroupDeleteDialog = function(group) {
		ModalService.showModal({
			templateUrl : "/assets/templates/delete-confirm-dialog.html",
			controller : "deleteConfirmDialogCtrl",
			inputs : {
				options : {
					headline : $translate.instant("group.headline.delete"),
					message : $translate.instant("group.message.delete").replace("{0}", group.name),
					object : angular.copy(group)
				},
			}
		}).then(function(modal) {
			modal.element.modal();
			modal.close.then(function(group) {
				if (group !== undefined) {
					$scope.deleteGroup(group);
				}
			});
		});
	};

	$scope.updateGroup = function(group) {
		if (group.id === undefined) {
			$http.post("/manage/groups/add", group).then(function(result) {
				if (result.status == 200) {
					$scope.groups.group.push(result.data);
				}
			}, function(e) {
				$rootScope.$emit("alertEvent", "error", e.data);
				$log.error(e);
			});
		} else {
			$http.post("/manage/groups/update", group).then(function(result) {
				if (result.status == 200) {
					group = result.data;
					for ( var i in $scope.groups.group) {
						if ($scope.groups.group[i].id == group.id) {
							$scope.groups.group[i] = group;
							return;
						}
					}
				}
			}, function(error) {
				$rootScope.$emit("alertEvent", "error", error.data);
				$log.error(error);
			});
		}
	};

	$scope.deleteGroup = function(group) {
		$http.post("/manage/groups/delete", group).then(function(result) {
			if (result.status == 200) {
				$scope.loadData();
			}
		}, function(error) {
			$rootScope.$emit("alertEvent", "error", error.data);
			$log.error(error);
		});
	};

	$scope.loadData();
});

app.controller("groupDialogCtrl", function($scope, group, users, close) {

	$scope.group = group;
	$scope.users = users;
	$scope.headline = "group.headline." + (group === undefined ? "create" : "edit");

	$scope.close = function(result) {
		close(result, 500);
	};

	$scope.save = function() {
		close($scope.group, 500);
	};
});

// jQuery Helpers
jQuery(document).ready(function() {
	jQuery(this).click(function(ev) {
		var $this = jQuery(ev.target);
		if ($this.data("collapse-hide") !== undefined) {
			jQuery($this.data("collapse-hide")).collapse("hide");
		}
	});
});