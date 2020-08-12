<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
</head>
<body>
<div>
    <div style="background:#ffffff;padding:15px">
        <center>
            <div style="width:100%;max-width:500px;background:#093935;height:165px">
                <br>
                <center>

                    <a href="#">
                        <img
                                alt=""
                                src='{!! asset("public/images/logo/logo.png") !!}'
                                width="200" height="130"
                                style="display:block;font-family:Helvetica,Arial,sans-serif;color:#666666;font-size:16px"
                                border="0" class="CToWUd">
                    </a>
                </center>
            </div>
            <br>

            <div style="width:100%;max-width:580px;background:#ffffff;height:auto;padding:10px 0 10px 0">
                <hr style="border:dashed 1px #e1e1e1;max-width:100%">

                <div style="width:100%;max-width:580px;background:#ffffff;height:auto;padding:0px 0 0px 0">
                    <div style="font-family:'Lato',Helvetica,Arial,sans-serif;display:inline-block;margin:0px 0px 0 0;max-width:100%;width:100%;margin-right:0px">

                        <div style="width:100%;max-width:580px;background:#ffffff;height:auto;padding:20px 0 0px 0">
                            <div bgcolor="#f8f4e8" align="left"
                                 style="padding:0px 0% 0px 0%;font-size:22px;line-height:25px;font-family:'Lato',Helvetica,Arial,sans-serif;color:#093935;font-weight:700"
                                 class="m_-7788511936867687679padding-copy">Dear Admin,
                            </div>


                            <div style="width:100%;max-width:580px;background:#ffffff;height:auto;padding:15px 0 0px 0">
                                <div bgcolor="#f8f4e8" align="left"
                                     style="padding:0px 0% 0px 0%;font-size:16px;line-height:25px;font-family:'Lato',Helvetica,Arial,sans-serif;color:#6c6e6e;font-weight:500"
                                     class="m_-7788511936867687679padding-copy">
                                     <b style="color: #093935;">{{$name}}</b> needs for help regarding <b style="color: #093935;">{{$subject}}</b><br>
                                    Email ID is : <b style="color: #093935;">{{$email}}</b><br>
                                    Mobile Number is : <b style="color: #093935;">{{$mobile}}</b><br>
                                    Message : <b style="color: #093935;">{{$msg}}</b><br>
                                </div>




                                <div style="width:100%;max-width:580px;background:#ffffff;height:auto;padding:0px 0 10px 0">
                                    <hr style="border:dashed 1px #e1e1e1;max-width:100%">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </center>
    </div>

</div>
</body>
</html>
