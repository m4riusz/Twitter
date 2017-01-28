<html>
<body>
<h3>Hi, ${suspectedUser.username}!</h3>

You have been reported!
<p><strong>Judge: </strong>${judge.username}</p>
<p><strong>Category: </strong>${category}</p>
<p>Status: <strong>${status}</strong></p>
<#if banDate??>
Administration decided to lock your account.
<p><strong>Banned until: </strong>${banDate?datetime}</p>
</#if>
</body>
</html>