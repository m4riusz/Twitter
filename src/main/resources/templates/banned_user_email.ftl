<html>
<body>
<h3>Hi, ${suspectedUser.username}!</h3>

You have been reported to court!
<p><strong>Judge: </strong>${judge.username}</p>
<p><strong>Category: </strong>${category}</p>
<h3>${status}</h3>
<#if banDate??>
<p><strong>Banned until: </strong>${banDate?datetime}</p>
</#if>
</body>
</html>