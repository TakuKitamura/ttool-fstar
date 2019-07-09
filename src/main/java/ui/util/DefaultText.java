<!DOCTYPE html>
<html class="" lang="en">
<head prefix="og: http://ogp.me/ns#">
<meta charset="utf-8">
<meta content="IE=edge" http-equiv="X-UA-Compatible">
<meta content="object" property="og:type">
<meta content="GitLab" property="og:site_name">
<meta content="src/main/java/ui/util/DefaultText.java · master · mbe-tools / TTool" property="og:title">
<meta content="TTool (pronounced &quot;tea-tool&quot;) is a toolkit dedicated to the edition of UML and SysML diagrams, and to the simulation and formal verification (safety, security, performance) of those diagrams. See ttool.telecom-paristech.fr..." property="og:description">
<meta content="/uploads/-/system/project/avatar/225/ttool.png" property="og:image">
<meta content="64" property="og:image:width">
<meta content="64" property="og:image:height">
<meta content="https://gitlab.telecom-paristech.fr/mbe-tools/TTool/blob/master/src/main/java/ui/util/DefaultText.java" property="og:url">
<meta content="summary" property="twitter:card">
<meta content="src/main/java/ui/util/DefaultText.java · master · mbe-tools / TTool" property="twitter:title">
<meta content="TTool (pronounced &quot;tea-tool&quot;) is a toolkit dedicated to the edition of UML and SysML diagrams, and to the simulation and formal verification (safety, security, performance) of those diagrams. See ttool.telecom-paristech.fr..." property="twitter:description">
<meta content="/uploads/-/system/project/avatar/225/ttool.png" property="twitter:image">

<title>src/main/java/ui/util/DefaultText.java · master · mbe-tools / TTool · GitLab</title>
<meta content="TTool (pronounced &quot;tea-tool&quot;) is a toolkit dedicated to the edition of UML and SysML diagrams, and to the simulation and formal verification (safety, security, performance) of those diagrams. See ttool.telecom-paristech.fr..." name="description">
<link rel="shortcut icon" type="image/png" href="/assets/favicon-7901bd695fb93edb07975966062049829afb56cf11511236e61bcf425070e36e.png" id="favicon" data-original-href="/assets/favicon-7901bd695fb93edb07975966062049829afb56cf11511236e61bcf425070e36e.png" />
<link rel="stylesheet" media="all" href="/assets/application-3cbf1ae156fa85f16d4ca01321e0965db8cfb9239404aaf52c3cebfc5b4493fb.css" />
<link rel="stylesheet" media="print" href="/assets/print-c8ff536271f8974b8a9a5f75c0ca25d2b8c1dceb4cff3c01d1603862a0bdcbfc.css" />



<link rel="stylesheet" media="all" href="/assets/highlight/themes/white-a165d47ce52cf24c29686366976ae691bd9addb9641a6abeb3ba6d1823b89aa8.css" />
<script>
//<![CDATA[
window.gon={};gon.api_version="v4";gon.default_avatar_url="https://gitlab.telecom-paristech.fr/assets/no_avatar-849f9c04a3a0d0cea2424ae97b27447dc64a7dbfae83c036c45b403392f0e8ba.png";gon.max_file_size=10;gon.asset_host=null;gon.webpack_public_path="/assets/webpack/";gon.relative_url_root="";gon.shortcuts_path="/help/shortcuts";gon.user_color_scheme="white";gon.gitlab_url="https://gitlab.telecom-paristech.fr";gon.revision="36c7ae2";gon.gitlab_logo="/assets/gitlab_logo-7ae504fe4f68fdebb3c2034e36621930cd36ea87924c11ff65dbcb8ed50dca58.png";gon.sprite_icons="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg";gon.sprite_file_icons="/assets/file_icons-7262fc6897e02f1ceaf8de43dc33afa5e4f9a2067f4f68ef77dcc87946575e9e.svg";gon.emoji_sprites_css_path="/assets/emoji_sprites-289eccffb1183c188b630297431be837765d9ff4aed6130cf738586fb307c170.css";gon.test_env=false;gon.suggested_label_colors=["#0033CC","#428BCA","#44AD8E","#A8D695","#5CB85C","#69D100","#004E00","#34495E","#7F8C8D","#A295D6","#5843AD","#8E44AD","#FFECDB","#AD4363","#D10069","#CC0033","#FF0000","#D9534F","#D1D100","#F0AD4E","#AD8D43"];gon.first_day_of_week=0;gon.ee=false;
//]]>
</script>


<script src="/assets/webpack/runtime.70ab43a8.bundle.js" defer="defer"></script>
<script src="/assets/webpack/main.35060dbf.chunk.js" defer="defer"></script>
<script src="/assets/webpack/commons~pages.groups~pages.groups.activity~pages.groups.boards~pages.groups.clusters.destroy~pages.g~c70c8c29.3f6ba0d1.chunk.js" defer="defer"></script>
<script src="/assets/webpack/commons~pages.groups.milestones.edit~pages.groups.milestones.new~pages.projects.blame.show~pages.pro~bedd5722.cca2d798.chunk.js" defer="defer"></script>
<script src="/assets/webpack/pages.projects.blob.show.84ae34be.chunk.js" defer="defer"></script>

<meta name="csrf-param" content="authenticity_token" />
<meta name="csrf-token" content="dCHRJlP102ZnzaNK7nhpkRRiXFNI7dOVZSrSW+eEMqemq7N1y6Rz1z9fYOUyMRqv1QzSyY9qmNAx8sy2vXq3zw==" />
<meta content="origin-when-cross-origin" name="referrer">
<meta content="width=device-width, initial-scale=1, maximum-scale=1" name="viewport">
<meta content="#474D57" name="theme-color">
<link rel="apple-touch-icon" type="image/x-icon" href="/assets/touch-icon-iphone-5a9cee0e8a51212e70b90c87c12f382c428870c0ff67d1eb034d884b78d2dae7.png" />
<link rel="apple-touch-icon" type="image/x-icon" href="/assets/touch-icon-ipad-a6eec6aeb9da138e507593b464fdac213047e49d3093fc30e90d9a995df83ba3.png" sizes="76x76" />
<link rel="apple-touch-icon" type="image/x-icon" href="/assets/touch-icon-iphone-retina-72e2aadf86513a56e050e7f0f2355deaa19cc17ed97bbe5147847f2748e5a3e3.png" sizes="120x120" />
<link rel="apple-touch-icon" type="image/x-icon" href="/assets/touch-icon-ipad-retina-8ebe416f5313483d9c1bc772b5bbe03ecad52a54eba443e5215a22caed2a16a2.png" sizes="152x152" />
<link color="rgb(226, 67, 41)" href="/assets/logo-d36b5212042cebc89b96df4bf6ac24e43db316143e89926c0db839ff694d2de4.svg" rel="mask-icon">
<meta content="/assets/msapplication-tile-1196ec67452f618d39cdd85e2e3a542f76574c071051ae7effbfde01710eb17d.png" name="msapplication-TileImage">
<meta content="#30353E" name="msapplication-TileColor">



</head>

<body class="ui-indigo  gl-browser-firefox gl-platform-linux" data-find-file="/mbe-tools/TTool/find_file/master" data-group="" data-page="projects:blob:show" data-project="TTool">

<script>
  gl = window.gl || {};
  gl.client = {"isFirefox":true,"isLinux":true};
</script>



<header class="navbar navbar-gitlab qa-navbar navbar-expand-sm js-navbar">
<a class="sr-only gl-accessibility" href="#content-body" tabindex="1">Skip to content</a>
<div class="container-fluid">
<div class="header-content">
<div class="title-container">
<h1 class="title">
<a title="Dashboard" id="logo" href="/"><img class="brand-header-logo lazy" data-src="/uploads/-/system/appearance/header_logo/1/TPT.png" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" />
</a></h1>
<ul class="list-unstyled navbar-sub-nav">
<li class="home"><a title="Projects" class="dashboard-shortcuts-projects" href="/explore">Projects
</a></li><li class=""><a title="Groups" class="dashboard-shortcuts-groups" href="/explore/groups">Groups
</a></li><li class=""><a title="Snippets" class="dashboard-shortcuts-snippets" href="/explore/snippets">Snippets
</a></li><li>
<a title="About GitLab CE" href="/help">Help</a>
</li>
</ul>

</div>
<div class="navbar-collapse collapse">
<ul class="nav navbar-nav">
<li class="nav-item d-none d-sm-none d-md-block m-auto">
<div class="search search-form" data-track-event="activate_form_input" data-track-label="navbar_search">
<form class="form-inline" action="/search" accept-charset="UTF-8" method="get"><input name="utf8" type="hidden" value="&#x2713;" /><div class="search-input-container">
<div class="search-input-wrap">
<div class="dropdown" data-url="/search/autocomplete">
<input type="search" name="search" id="search" placeholder="Search or jump to…" class="search-input dropdown-menu-toggle no-outline js-search-dashboard-options" spellcheck="false" tabindex="1" autocomplete="off" data-issues-path="/dashboard/issues" data-mr-path="/dashboard/merge_requests" aria-label="Search or jump to…" />
<button class="hidden js-dropdown-search-toggle" data-toggle="dropdown" type="button"></button>
<div class="dropdown-menu dropdown-select">
<div class="dropdown-content"><ul>
<li class="dropdown-menu-empty-item">
<a>
Loading...
</a>
</li>
</ul>
</div><div class="dropdown-loading"><i aria-hidden="true" data-hidden="true" class="fa fa-spinner fa-spin"></i></div>
</div>
<svg class="s16 search-icon"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#search"></use></svg>
<svg class="s16 clear-icon js-clear-input"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#close"></use></svg>
</div>
</div>
</div>
<input type="hidden" name="group_id" id="group_id" class="js-search-group-options" />
<input type="hidden" name="project_id" id="search_project_id" value="225" class="js-search-project-options" data-project-path="TTool" data-name="TTool" data-issues-path="/mbe-tools/TTool/issues" data-mr-path="/mbe-tools/TTool/merge_requests" data-issues-disabled="false" />
<input type="hidden" name="search_code" id="search_code" value="true" />
<input type="hidden" name="repository_ref" id="repository_ref" value="master" />

<div class="search-autocomplete-opts hide" data-autocomplete-path="/search/autocomplete" data-autocomplete-project-id="225" data-autocomplete-project-ref="master"></div>
</form></div>

</li>
<li class="nav-item d-inline-block d-sm-none d-md-none">
<a title="Search" aria-label="Search" data-toggle="tooltip" data-placement="bottom" data-container="body" href="/search?project_id=225"><svg class="s16"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#search"></use></svg>
</a></li>
<li class="nav-item header-help dropdown">
<a class="header-help-dropdown-toggle" data-toggle="dropdown" href="/help"><svg class="s16"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#question"></use></svg>
<svg class="caret-down"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#angle-down"></use></svg>
</a><div class="dropdown-menu dropdown-menu-right">
<ul>
<li>
<a href="/help">Help</a>
</li>
<li class="divider"></li>
<li>
<a href="https://about.gitlab.com/submit-feedback">Submit feedback</a>
</li>
<li>
<a target="_blank" class="text-nowrap" href="https://about.gitlab.com/contributing">Contribute to GitLab
</a></li>

</ul>

</div>
</li>
<li class="nav-item">
<div>
<a class="btn btn-sign-in" href="/users/sign_in?redirect_to_referer=yes">Sign in</a>
</div>
</li>
</ul>
</div>
<button class="navbar-toggler d-block d-sm-none" type="button">
<span class="sr-only">Toggle navigation</span>
<svg class="s12 more-icon js-navbar-toggle-right"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#ellipsis_h"></use></svg>
<svg class="s12 close-icon js-navbar-toggle-left"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#close"></use></svg>
</button>
</div>
</div>
</header>

<div class="layout-page page-with-contextual-sidebar">
<div class="nav-sidebar">
<div class="nav-sidebar-inner-scroll">
<div class="context-header">
<a title="TTool" href="/mbe-tools/TTool"><div class="avatar-container rect-avatar s40 project-avatar">
<img alt="TTool" class="avatar s40 avatar-tile lazy" width="40" height="40" data-src="/uploads/-/system/project/avatar/225/ttool.png" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" />
</div>
<div class="sidebar-context-title">
TTool
</div>
</a></div>
<ul class="sidebar-top-level-items">
<li class="home"><a class="shortcuts-project" href="/mbe-tools/TTool"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#home"></use></svg>
</div>
<span class="nav-item-name">
Project
</span>
</a><ul class="sidebar-sub-level-items">
<li class="fly-out-top-item"><a href="/mbe-tools/TTool"><strong class="fly-out-top-item-name">
Project
</strong>
</a></li><li class="divider fly-out-top-item"></li>
<li class=""><a title="Project details" class="shortcuts-project" href="/mbe-tools/TTool"><span>Details</span>
</a></li><li class=""><a title="Activity" class="shortcuts-project-activity qa-activity-link" href="/mbe-tools/TTool/activity"><span>Activity</span>
</a></li><li class=""><a title="Releases" class="shortcuts-project-releases" href="/mbe-tools/TTool/releases"><span>Releases</span>
</a></li>
<li class=""><a title="Cycle Analytics" class="shortcuts-project-cycle-analytics" href="/mbe-tools/TTool/cycle_analytics"><span>Cycle Analytics</span>
</a></li></ul>
</li><li class="active"><a class="shortcuts-tree qa-project-menu-repo" href="/mbe-tools/TTool/tree/master"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#doc-text"></use></svg>
</div>
<span class="nav-item-name">
Repository
</span>
</a><ul class="sidebar-sub-level-items">
<li class="fly-out-top-item active"><a href="/mbe-tools/TTool/tree/master"><strong class="fly-out-top-item-name">
Repository
</strong>
</a></li><li class="divider fly-out-top-item"></li>
<li class="active"><a href="/mbe-tools/TTool/tree/master">Files
</a></li><li class=""><a href="/mbe-tools/TTool/commits/master">Commits
</a></li><li class=""><a class="qa-branches-link" href="/mbe-tools/TTool/branches">Branches
</a></li><li class=""><a href="/mbe-tools/TTool/tags">Tags
</a></li><li class=""><a href="/mbe-tools/TTool/graphs/master">Contributors
</a></li><li class=""><a href="/mbe-tools/TTool/network/master">Graph
</a></li><li class=""><a href="/mbe-tools/TTool/compare?from=master&amp;to=master">Compare
</a></li><li class=""><a href="/mbe-tools/TTool/graphs/master/charts">Charts
</a></li>
</ul>
</li><li class=""><a class="shortcuts-issues qa-issues-item" href="/mbe-tools/TTool/issues"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#issues"></use></svg>
</div>
<span class="nav-item-name">
Issues
</span>
<span class="badge badge-pill count issue_counter">
49
</span>
</a><ul class="sidebar-sub-level-items">
<li class="fly-out-top-item"><a href="/mbe-tools/TTool/issues"><strong class="fly-out-top-item-name">
Issues
</strong>
<span class="badge badge-pill count issue_counter fly-out-badge">
49
</span>
</a></li><li class="divider fly-out-top-item"></li>
<li class=""><a title="Issues" href="/mbe-tools/TTool/issues"><span>
List
</span>
</a></li><li class=""><a title="Board" href="/mbe-tools/TTool/boards"><span>
Board
</span>
</a></li><li class=""><a title="Labels" class="qa-labels-link" href="/mbe-tools/TTool/labels"><span>
Labels
</span>
</a></li>
<li class=""><a title="Milestones" class="qa-milestones-link" href="/mbe-tools/TTool/milestones"><span>
Milestones
</span>
</a></li></ul>
</li><li class=""><a class="shortcuts-merge_requests qa-merge-requests-link" href="/mbe-tools/TTool/merge_requests"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#git-merge"></use></svg>
</div>
<span class="nav-item-name">
Merge Requests
</span>
<span class="badge badge-pill count merge_counter js-merge-counter">
2
</span>
</a><ul class="sidebar-sub-level-items is-fly-out-only">
<li class="fly-out-top-item"><a href="/mbe-tools/TTool/merge_requests"><strong class="fly-out-top-item-name">
Merge Requests
</strong>
<span class="badge badge-pill count merge_counter js-merge-counter fly-out-badge">
2
</span>
</a></li></ul>
</li><li class=""><a class="shortcuts-wiki qa-wiki-link" href="/mbe-tools/TTool/wikis/home"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#book"></use></svg>
</div>
<span class="nav-item-name">
Wiki
</span>
</a><ul class="sidebar-sub-level-items is-fly-out-only">
<li class="fly-out-top-item"><a href="/mbe-tools/TTool/wikis/home"><strong class="fly-out-top-item-name">
Wiki
</strong>
</a></li></ul>
</li><li class=""><a title="Members" class="shortcuts-tree" href="/mbe-tools/TTool/settings/members"><div class="nav-icon-container">
<svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#users"></use></svg>
</div>
<span class="nav-item-name">
Members
</span>
</a><ul class="sidebar-sub-level-items is-fly-out-only">
<li class="fly-out-top-item"><a href="/mbe-tools/TTool/project_members"><strong class="fly-out-top-item-name">
Members
</strong>
</a></li></ul>
</li><a class="toggle-sidebar-button js-toggle-sidebar" role="button" title="Toggle sidebar" type="button">
<svg class="icon-angle-double-left"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#angle-double-left"></use></svg>
<svg class="icon-angle-double-right"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#angle-double-right"></use></svg>
<span class="collapse-text">Collapse sidebar</span>
</a>
<button name="button" type="button" class="close-nav-button"><svg class="s16"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#close"></use></svg>
<span class="collapse-text">Close sidebar</span>
</button>
<li class="hidden">
<a title="Activity" class="shortcuts-project-activity" href="/mbe-tools/TTool/activity"><span>
Activity
</span>
</a></li>
<li class="hidden">
<a title="Network" class="shortcuts-network" href="/mbe-tools/TTool/network/master">Graph
</a></li>
<li class="hidden">
<a title="Charts" class="shortcuts-repository-charts" href="/mbe-tools/TTool/graphs/master/charts">Charts
</a></li>
<li class="hidden">
<a class="shortcuts-new-issue" href="/mbe-tools/TTool/issues/new">Create a new issue
</a></li>
<li class="hidden">
<a title="Commits" class="shortcuts-commits" href="/mbe-tools/TTool/commits/master">Commits
</a></li>
<li class="hidden">
<a title="Issue Boards" class="shortcuts-issue-boards" href="/mbe-tools/TTool/boards">Issue Boards</a>
</li>
</ul>
</div>
</div>

<div class="content-wrapper">

<div class="mobile-overlay"></div>
<div class="alert-wrapper">




<nav class="breadcrumbs container-fluid container-limited" role="navigation">
<div class="breadcrumbs-container">
<button name="button" type="button" class="toggle-mobile-nav"><span class="sr-only">Open sidebar</span>
<i aria-hidden="true" data-hidden="true" class="fa fa-bars"></i>
</button><div class="breadcrumbs-links js-title-container">
<ul class="list-unstyled breadcrumbs-list js-breadcrumbs-list">
<li><a class="group-path breadcrumb-item-text js-breadcrumb-item-text " href="/mbe-tools">mbe-tools</a><svg class="s8 breadcrumbs-list-angle"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#angle-right"></use></svg></li> <li><a href="/mbe-tools/TTool"><img alt="TTool" class="avatar-tile lazy" width="15" height="15" data-src="/uploads/-/system/project/avatar/225/ttool.png" src="data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==" /><span class="breadcrumb-item-text js-breadcrumb-item-text">TTool</span></a><svg class="s8 breadcrumbs-list-angle"><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#angle-right"></use></svg></li>

<li>
<h2 class="breadcrumbs-sub-title"><a href="/mbe-tools/TTool/blob/master/src/main/java/ui/util/DefaultText.java">Repository</a></h2>
</li>
</ul>
</div>

</div>
</nav>

<div class="flash-container flash-container-page">
</div>

<div class="d-flex"></div>
</div>
<div class=" ">
<div class="content" id="content-body">
<div class="js-signature-container" data-signatures-path="/mbe-tools/TTool/commits/5e5ff3fd541eb715121b02b0a53f4f4b6a9d05ab/signatures"></div>
<div class="container-fluid container-limited">

<div class="tree-holder" id="tree-holder">
<div class="nav-block">
<div class="tree-ref-container">
<div class="tree-ref-holder">
<form class="project-refs-form" action="/mbe-tools/TTool/refs/switch" accept-charset="UTF-8" method="get"><input name="utf8" type="hidden" value="&#x2713;" /><input type="hidden" name="destination" id="destination" value="blob" />
<input type="hidden" name="path" id="path" value="src/main/java/ui/util/DefaultText.java" />
<div class="dropdown">
<button class="dropdown-menu-toggle js-project-refs-dropdown qa-branches-select" type="button" data-toggle="dropdown" data-selected="master" data-ref="master" data-refs-url="/mbe-tools/TTool/refs?sort=updated_desc" data-field-name="ref" data-submit-form-on-click="true" data-visit="true"><span class="dropdown-toggle-text ">master</span><i aria-hidden="true" data-hidden="true" class="fa fa-chevron-down"></i></button>
<div class="dropdown-menu dropdown-menu-paging dropdown-menu-selectable git-revision-dropdown qa-branches-dropdown">
<div class="dropdown-page-one">
<div class="dropdown-title"><span>Switch branch/tag</span><button class="dropdown-title-button dropdown-menu-close" aria-label="Close" type="button"><i aria-hidden="true" data-hidden="true" class="fa fa-times dropdown-menu-close-icon"></i></button></div>
<div class="dropdown-input"><input type="search" id="" class="dropdown-input-field" placeholder="Search branches and tags" autocomplete="off" /><i aria-hidden="true" data-hidden="true" class="fa fa-search dropdown-input-search"></i><i aria-hidden="true" data-hidden="true" role="button" class="fa fa-times dropdown-input-clear js-dropdown-input-clear"></i></div>
<div class="dropdown-content"></div>
<div class="dropdown-loading"><i aria-hidden="true" data-hidden="true" class="fa fa-spinner fa-spin"></i></div>
</div>
</div>
</div>
</form>
</div>
<ul class="breadcrumb repo-breadcrumb">
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master">TTool
</a></li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master/src">src</a>
</li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master/src/main">main</a>
</li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master/src/main/java">java</a>
</li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master/src/main/java/ui">ui</a>
</li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/tree/master/src/main/java/ui/util">util</a>
</li>
<li class="breadcrumb-item">
<a href="/mbe-tools/TTool/blob/master/src/main/java/ui/util/DefaultText.java"><strong>DefaultText.java</strong>
</a></li>
</ul>
</div>
<div class="tree-controls">
<a class="btn shortcuts-find-file" rel="nofollow" href="/mbe-tools/TTool/find_file/master"><i aria-hidden="true" data-hidden="true" class="fa fa-search"></i>
<span>Find file</span>
</a>
<div class="btn-group" role="group"><a class="btn js-blob-blame-link" href="/mbe-tools/TTool/blame/master/src/main/java/ui/util/DefaultText.java">Blame</a><a class="btn" href="/mbe-tools/TTool/commits/master/src/main/java/ui/util/DefaultText.java">History</a><a class="btn js-data-file-blob-permalink-url" href="/mbe-tools/TTool/blob/5e5ff3fd541eb715121b02b0a53f4f4b6a9d05ab/src/main/java/ui/util/DefaultText.java">Permalink</a></div>
</div>
</div>

<div class="info-well d-none d-sm-block">
<div class="well-segment">
<ul class="blob-commit-info">
<li class="commit flex-row js-toggle-container" id="commit-5e5ff3fd">
<div class="avatar-cell d-none d-sm-block">
<a href="mailto:apvrille@cadillac.eurecom.fr"><img alt="Ludovic Apvrille&#39;s avatar" src="https://secure.gravatar.com/avatar/c075ea17393fdd0d4f4445366dcd2669?s=72&amp;d=identicon" class="avatar s36 d-none d-sm-inline" title="Ludovic Apvrille" /></a>
</div>
<div class="commit-detail flex-list">
<div class="commit-content qa-commit-content">
<a class="commit-row-message item-title" href="/mbe-tools/TTool/commit/5e5ff3fd541eb715121b02b0a53f4f4b6a9d05ab">update on build version: build.txt</a>
<span class="commit-row-message d-block d-sm-none">
&middot;
5e5ff3fd
</span>
<div class="committer">
<a class="commit-author-link" href="mailto:apvrille@cadillac.eurecom.fr">Ludovic Apvrille</a> authored <time class="js-timeago" title="Jul 9, 2019 3:02am" datetime="2019-07-09T01:02:18Z" data-toggle="tooltip" data-placement="bottom" data-container="body">Jul 09, 2019</time>
</div>
</div>
<div class="commit-actions flex-row d-none d-sm-flex">

<div class="js-commit-pipeline-status" data-endpoint="/mbe-tools/TTool/commit/5e5ff3fd541eb715121b02b0a53f4f4b6a9d05ab/pipelines?ref=master"></div>
<div class="commit-sha-group">
<div class="label label-monospace">
5e5ff3fd
</div>
<button class="btn btn btn-default" data-toggle="tooltip" data-placement="bottom" data-container="body" data-title="Copy commit SHA to clipboard" data-class="btn btn-default" data-clipboard-text="5e5ff3fd541eb715121b02b0a53f4f4b6a9d05ab" type="button" title="Copy commit SHA to clipboard" aria-label="Copy commit SHA to clipboard"><svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#duplicate"></use></svg></button>

</div>
</div>
</div>
</li>

</ul>
</div>


</div>
<div class="blob-content-holder" id="blob-content-holder">
<article class="file-holder">
<div class="js-file-title file-title-flex-parent">
<div class="file-header-content">
<i aria-hidden="true" data-hidden="true" class="fa fa-file-text-o fa-fw"></i>
<strong class="file-title-name">
DefaultText.java
</strong>
<button class="btn btn-clipboard btn-transparent prepend-left-5" data-toggle="tooltip" data-placement="bottom" data-container="body" data-class="btn-clipboard btn-transparent prepend-left-5" data-title="Copy file path to clipboard" data-clipboard-text="{&quot;text&quot;:&quot;src/main/java/ui/util/DefaultText.java&quot;,&quot;gfm&quot;:&quot;`src/main/java/ui/util/DefaultText.java`&quot;}" type="button" title="Copy file path to clipboard" aria-label="Copy file path to clipboard"><svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#duplicate"></use></svg></button>
<small>
3.27 KB
</small>
</div>

<div class="file-actions">

<div class="btn-group" role="group"><button class="btn btn btn-sm js-copy-blob-source-btn" data-toggle="tooltip" data-placement="bottom" data-container="body" data-class="btn btn-sm js-copy-blob-source-btn" data-title="Copy source to clipboard" data-clipboard-target=".blob-content[data-blob-id=&#39;3f8410c7bae8cc6a65cc638394c2025ccca5c39f&#39;]" type="button" title="Copy source to clipboard" aria-label="Copy source to clipboard"><svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#duplicate"></use></svg></button><a class="btn btn-sm has-tooltip" target="_blank" rel="noopener noreferrer" title="Open raw" data-container="body" href="/mbe-tools/TTool/raw/master/src/main/java/ui/util/DefaultText.java"><i aria-hidden="true" data-hidden="true" class="fa fa-file-code-o"></i></a><a download="src/main/java/ui/util/DefaultText.java" class="btn btn-sm has-tooltip" target="_blank" rel="noopener noreferrer" title="Download" data-container="body" href="/mbe-tools/TTool/raw/master/src/main/java/ui/util/DefaultText.java?inline=false"><svg><use xlink:href="/assets/icons-24aaa921aa9e411162e6913688816c79861d0de4bee876cf6fc4c794be34ee91.svg#download"></use></svg></a></div>
<div class="btn-group" role="group"><a class="btn js-edit-blob  btn-sm" href="/mbe-tools/TTool/edit/master/src/main/java/ui/util/DefaultText.java">Edit</a><a class="btn btn-default btn-sm" href="/-/ide/project/mbe-tools/TTool/edit/master/-/src/main/java/ui/util/DefaultText.java">Web IDE</a></div>
</div>
</div>



<div class="blob-viewer" data-type="simple" data-url="/mbe-tools/TTool/blob/master/src/main/java/ui/util/DefaultText.java?format=json&amp;viewer=simple">
<div class="text-center prepend-top-default append-bottom-default">
<i aria-hidden="true" aria-label="Loading content…" class="fa fa-spinner fa-spin fa-2x qa-spinner"></i>
</div>

</div>


</article>
</div>

<div class="modal" id="modal-upload-blob">
<div class="modal-dialog modal-lg">
<div class="modal-content">
<div class="modal-header">
<h3 class="page-title">Replace DefaultText.java</h3>
<button aria-label="Close" class="close" data-dismiss="modal" type="button">
<span aria-hidden="true">&times;</span>
</button>
</div>
<div class="modal-body">
<form class="js-quick-submit js-upload-blob-form" data-method="put" action="/mbe-tools/TTool/update/master/src/main/java/ui/util/DefaultText.java" accept-charset="UTF-8" method="post"><input name="utf8" type="hidden" value="&#x2713;" /><input type="hidden" name="_method" value="put" /><input type="hidden" name="authenticity_token" value="0VtVnAjDj7Psi4GosLSFwOsJHmgt9+YOQ0QAZCdSBNED0TfPkJIvArQZQgds/fb+KmeQ8upwrUsXnB6JfayBuQ==" /><div class="dropzone">
<div class="dropzone-previews blob-upload-dropzone-previews">
<p class="dz-message light">
Attach a file by drag &amp; drop or <a class="markdown-selector" href="#">click to upload</a>
</p>
</div>
</div>
<br>
<div class="dropzone-alerts alert alert-danger data" style="display:none"></div>
<div class="form-group row commit_message-group">
<label class="col-form-label col-sm-2" for="commit_message-41b68e565d04620f01ce6ef4125ce623">Commit message
</label><div class="col-sm-10">
<div class="commit-message-container">
<div class="max-width-marker"></div>
<textarea name="commit_message" id="commit_message-41b68e565d04620f01ce6ef4125ce623" class="form-control js-commit-message" placeholder="Replace DefaultText.java" required="required" rows="3">
Replace DefaultText.java</textarea>
</div>
</div>
</div>

<input type="hidden" name="branch_name" id="branch_name" />
<input type="hidden" name="create_merge_request" id="create_merge_request" value="1" />
<input type="hidden" name="original_branch" id="original_branch" value="master" class="js-original-branch" />

<div class="form-actions">
<button name="button" type="button" class="btn btn-success btn-upload-file" id="submit-all"><i aria-hidden="true" data-hidden="true" class="fa fa-spin fa-spinner js-loading-icon hidden"></i>
Replace file
</button><a class="btn btn-cancel" data-dismiss="modal" href="#">Cancel</a>
<div class="inline prepend-left-10">
A new branch will be created in your fork and a new merge request will be started.
</div>

</div>
</form></div>
</div>
</div>
</div>

</div>
</div>

</div>
</div>
</div>
</div>



</body>
</html>

