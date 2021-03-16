<head>
	<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
	<title>My Home Page</title>
	<style>
		.bd-placeholder-img {
			font-size: 1.125rem;
			text-anchor: middle;
			-webkit-user-select: none;
			-moz-user-select: none;
			-ms-user-select: none;
			user-select: none;
		}

		@media (min-width: 768px) {
			.bd-placeholder-img-lg {
				font-size: 3.5rem;
			}
		}
	</style>
</head>
<body>
<div class="container">
	<form class="form-signin" action="/register/validate">
		<h1 class="h3 mb-3 font-weight-normal">注册</h1>
		<input
				type="email" id="inputEmail" class="form-control"
				placeholder="请输入电子邮件" name="inputEmail" required autofocus>
		<label
				for="inputPassword" class="sr-only">密码</label>
		<input
				type="password" id="inputPassword" class="form-control"
				placeholder="请输入密码" name="inputPassword" required>
		<label for="inputPhone" class="sr-only">电话</label>
		<input
				type="tel" id="inputPhone" class="form-control"
				placeholder="请输入电话" name="inputPhone" required autofocus>
		<label for="inputName" class="sr-only">姓名</label>
		<input
				type="text" id="inputName" class="form-control"
				placeholder="请输入姓名" name="inputName" required autofocus>
		<input class="btn btn-lg btn-primary btn-block" type="submit" name="Register"/>
		<p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
	</form>
</div>
</body>