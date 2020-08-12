<?php

$env = file_get_contents('../.env');
if (strpos($env, 'IS_SET=true') !== false) {
    header("Location: ../admin");
    exit;
}

require_once 'php/Verification.php';
require_once 'php/Validation.php';
require_once 'php/Requirements.php';
require_once 'php/DbImport.php';
require_once 'php/FileWrite.php';

$Requirements = new Requirements();
$DbImport = new DbImport();
$FileWrite = new FileWrite();
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="colorlib.com">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Sign Up Form</title>

    <!-- Main css -->
    <link href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.css' rel='stylesheet'>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

    <!-- MultiStep Form -->
    <div class="container-fluid login-wrap" id="grad1">
        <div class="row justify-content-center mt-0">
            <div class="col-11 col-sm-9 col-md-7 col-lg-6 text-center p-0 mt-3 mb-2">
                <div class="card px-0 pt-4 pb-0 mt-3 mb-3">
                    <h2><strong>Install</strong></h2>
                    <p>Fill all form field to go to next step</p>
                    <div class="row">
                        <div class="col-md-12 mx-0">
                            <form id="msform" method="get">
                                <!-- progressbar -->
                                <ul id="progressbar">
                                    <li class="active" id="account"><strong>Step 1</strong></li>
                                    <li id="personal"><strong>Step 2</strong></li>
                                    <li id="payment"><strong>Step 3</strong></li>
                                    <li id="confirm"><strong>Step 4</strong></li>
                                    <li id="complate"><strong>Step 5</strong></li>
                                </ul> <!-- fieldsets -->
                                <div id="success-msg" class="alert alert-dismissible mt-3" style="display: none;">
                                <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <strong>Message!</strong> <span id="msg"></span>
                                </div>
                                <fieldset class="step-1">
                                    <div class="form-card">
                                        <input type="text" id="userid" name="userid" value="userid" placeholder="Envato User Name" required>
                                        <input type="text" id="purchase_key" name="purchase_key" value="purchase_key" placeholder="Purchase Key" required>
                                    </div>
                                    <button type="button" name="submit" id="submit" class="action-button">Save</button>
                                    <!-- <input type="button" name="next" class="next action-button" value="Next Step" /> -->
                                </fieldset>
                                <fieldset class="step-2">
                                    <div class="form-card">
                                        <h3 class="title text-center"><strong>Directory permissions & requirements</strong></h3>
                                        <!-- display requirements -->
                                        <?=$Requirements->directoriesAndPermission();?>
                                        <!-- Server Requirement -->
                                        <div class="height"></div>
                                        <?=$Requirements->server();?>
                                    </div>
                                    <input type="button" name="previous" class="previous action-button-previous" value="Previous" />
                                    <input type="button" name="next" class="next action-button" value="Next Step" />
                                </fieldset>
                                <fieldset class="step-3">
                                    <div class="form-card">
                                        <div class="form-group">
                                            <label for="database">Database Name </label>
                                            <input type="text" name="database" class="form-control" id="database" placeholder="Database Name" value="">
                                        </div>
                                        <div class="form-group">
                                            <label for="username">Database Username </label>
                                            <input type="text" name="username" class="form-control" id="username" placeholder="Database Username" value="">
                                        </div>
                                        <div class="form-group">
                                            <label for="password">Database Password </label>
                                            <input type="password" name="password" class="form-control" id="password" placeholder="Database Password" value="">
                                        </div>
                                        <div class="form-group">
                                            <label for="hostname">Host Name
                                                <span class="glyphicon glyphicon-question-sign" data-toggle="tooltip" data-placement="bottom" title="Please keep localhost by default, but if need you can add your own domain name or ip address"></span>
                                            </label>
                                            <input type="text" name="hostname" class="form-control" id="hostname" placeholder="Host Name"  value="localhost">
                                        </div>
                                    </div>
                                    <input type="button" name="previous" class="previous action-button-previous" value="Previous" />
                                    <button type="button" name="database_conn" id="database_conn" class="action-button">Next</button>
                                </fieldset>
                                <fieldset class="step-4">
                                    <div class="form-card">
                                        <div class="form-group">
                                            <label for="email">Admin Username </label>
                                            <input type="text" name="name" class="form-control" id="name" placeholder="Admin Username" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="email">Admin Email </label>
                                            <input type="email" name="email" class="form-control" id="email" placeholder="Admin Email" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="password">Admin Password </label>
                                            <input type="password" name="password" class="form-control" placeholder="Admin Password" id="pass" required>
                                        </div>
                                    </div>
                                    <input type="button" name="previous" class="previous action-button-previous" value="Previous" />
                                    <button type="button" name="admindata" id="admindata" class="action-button">Next</button>
                                </fieldset>
                                <fieldset class="step-5">
                                    <div class="form-card">
                                        <h2 class="fs-title text-center">Success !</h2> <br><br>
                                        <div class="row justify-content-center">
                                            <div class="col-3">
                                                <img src="https://img.icons8.com/color/96/000000/ok--v2.png" class="fit-image">
                                            </div>
                                        </div> <br><br>
                                        <div class="row justify-content-center">
                                            <div class="col-7 text-center">
                                                <h5>You Have Successfully installed <br> <a href="../admin">Launch your App</a></h5>
                                            </div>
                                        </div>
                                    </div>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- JS -->
    <script type='text/javascript' src='https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>
    <script type='text/javascript' src='js/bootstrap.bundle.min.js'></script>
    <script src="js/main.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            $("#submit").click(function(){
                // alert('afad');
                var userid= $("#userid").val();
                var purchase_key= $("#purchase_key").val();
                var action= "add";

                $.ajax({
                    url:'ajax/verify.php',
                    method:'POST',
                    data: {
                        userid: userid,
                        purchase_key: purchase_key,
                        action: action,
                    },
                    cache: false,
                    dataType: "json",
                    success:function(response){
                        if (response['status'] == 1) {

                            setLocalData(1, {userid: userid,purchase_key: purchase_key});

                            $('#success-msg').removeClass('alert-danger');
                            $('#success-msg').addClass('alert-success');
                            $('#personal').addClass('active');
                            $('.step-1').css("display","none");
                            $('.step-2').css("display","block");
                            $('#success-msg').css("display","block");
                            $('#msg').text(response['msg']);
                            setTimeout(function() {
                                $("#success-msg").hide();
                            }, 5000);
                        } else {
                            $('#msg').text(response['msg']);
                            $('#success-msg').removeClass('alert-success');
                            $('#success-msg').addClass('alert-danger');
                            $('#success-msg').css("display","block");
                            setTimeout(function() {
                                $("#success-msg").hide();
                                // alert('test');
                            }, 5000);
                        }
                    }
                });
            });
        });


        $(document).ready(function(){
            $("#database_conn").click(function(){
                // alert('afad');
                var database= $("#database").val();
                var username= $("#username").val();
                var password= $("#password").val();
                var hostname= $("#hostname").val();
                var action= "database";

                $.ajax({
                    url:'ajax/verify.php',
                    method:'POST',
                    data: {
                        database: database,
                        username: username,
                        password: password,
                        hostname: hostname,
                        action: action,
                    },
                    cache: false,
                    dataType: "json",
                    success:function(response){
                        if (response['status'] == 1) {

                        setLocalData(2, {database: database,
                        username: username,
                        password: password,
                        hostname: hostname,
                        action: action});
                            $('#success-msg').removeClass('alert-danger');
                            $('#success-msg').addClass('alert-success');
                            $('#confirm').addClass('active');
                            $('.step-3').css("display","none");
                            $('.step-4').css("display","block");
                            $('#success-msg').css("display","block");
                            $('#msg').text(response['msg']);
                            setTimeout(function() {
                                $("#success-msg").hide();
                            }, 5000);
                        } else {
                            $('#success-msg').removeClass('alert-success');
                            $('#msg').text(response['msg']);
                            $('#success-msg').addClass('alert-danger');
                            $('#success-msg').css("display","block");
                            setTimeout(function() {
                                $("#success-msg").hide();
                                // alert('test');
                            }, 5000);
                        }
                    }
                });
            });
        });

        $(document).ready(function(){
            $("#admindata").click(function(){
                // alert('afad');
                var name= $("#name").val();
                var email= $("#email").val();
                var password= $("#pass").val();
                var action= "admindata";

                $.ajax({
                    url:'ajax/verify.php',
                    method:'POST',
                    data: {
                        name: name,
                        email: email,
                        password: password,
                        action: action,
                    },
                    cache: false,
                    dataType: "json",
                    success:function(response){
                        if (response['status'] == 1) {
                            $('#success-msg').removeClass('alert-danger');
                            $('#success-msg').addClass('alert-success');
                            $('#complate').addClass('active');
                            $('.step-4').css("display","none");
                            $('.step-5').css("display","block");
                            $('#success-msg').css("display","block");
                            $('#msg').text(response['msg']);

                            localStorage.clear();
                            setTimeout(function() {
                                $("#success-msg").hide();
                            }, 5000);
                        } else {
                            $('#success-msg').removeClass('alert-success');
                            $('#msg').text(response['msg']);
                            $('#success-msg').addClass('alert-danger');
                            $('#success-msg').css("display","block");
                            setTimeout(function() {
                                $("#success-msg").hide();
                                // alert('test');
                            }, 5000);
                        }
                    }
                });
            });
            let localData = JSON.parse(localStorage.getItem("Envatodata"));

            if (localData !== null && localData !== undefined) {

                localStorage.setItem("Envatodata", JSON.stringify(localData));
                checklactive (localData);
            }

        });

        function setLocalData(index, data) {
            let localData = JSON.parse(localStorage.getItem("Envatodata"));

            if (localData !== null && localData !== undefined) {
                localData[index] = data;
                localStorage.setItem("Envatodata", JSON.stringify(localData));
                checklactive (localData);
            }
            else {
                let obj = {
                    [index] : data
                }
                localStorage.setItem("Envatodata", JSON.stringify(obj));
            }
        }

        function checklactive(data) {
            if (data[2] && data[2] !== undefined) {

                $('#account').addClass('active');
                $('#personal').addClass('active');
                $('#payment').addClass('active');
                $('.step-1').css("display","none");
                $('.step-2').css("display","none");
                $('.step-3').css("display","block");

            } else if (data[1]) {

                $('#account').addClass('active');
                $('#personal').addClass('active');
                $('.step-1').css("display","none");
                $('.step-2').css("display","block");

            }
        }
    </script>
</body>
</html>