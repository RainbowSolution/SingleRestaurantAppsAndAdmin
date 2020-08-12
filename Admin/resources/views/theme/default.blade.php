<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>Admin</title>
    <!-- Favicon icon -->
    <link rel="icon" type="image/png" sizes="16x16" href="{!! asset('public/assets/images/favicon.png') !!}">
    <!-- Pignose Calender -->
    <link href="{!! asset('public/assets/plugins/pg-calendar/css/pignose.calendar.min.css') !!}" rel="stylesheet">
    <!-- Chartist -->
    <link rel="stylesheet" href="{!! asset('public/assets/plugins/chartist/css/chartist.min.css') !!}">
    <link rel="stylesheet" href="{!! asset('public/assets/plugins/chartist-plugin-tooltips/css/chartist-plugin-tooltip.css') !!}">

    <link href="{!! asset('public/assets/plugins/tables/css/datatable/dataTables.bootstrap4.min.css') !!}" rel="stylesheet">
    <!-- Custom Stylesheet -->
    <link href="{!! asset('public/assets/plugins/sweetalert/css/sweetalert.css') !!}" rel="stylesheet">
    <link href="{!! asset('public/assets/css/style.css') !!}" rel="stylesheet">

</head>
<body>

    <!--*******************
        Preloader start
    ********************-->
    <div id="preloader">
        <div class="loader">
            <svg class="circular" viewBox="25 25 50 50">
                <circle class="path" cx="50" cy="50" r="20" fill="none" stroke-width="3" stroke-miterlimit="10" />
            </svg>
        </div>
    </div>
    <!--*******************
        Preloader end
    ********************-->

    <div id="main-wrapper">

        @include('theme.header')
        @include('theme.sidebar')
        <div class="content-body">
            @yield('content')
        </div>
        <!-- /#page-wrapper -->
        <div class="card-content collapse show">
          <div class="card-body">
            <div class="row my-2">
              <div class="col-lg-4 col-md-6 col-sm-12">
                <div class="form-group">
                  <!-- Modal Change Password-->
                  <div class="modal fade text-left" id="ChangePasswordModal" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
                  aria-hidden="true">
                    <div class="modal-dialog" role="document">
                      <div class="modal-content">
                        <div class="modal-header">
                          <label class="modal-title text-text-bold-600" id="RditProduct">Change Password</label>
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                          </button>
                        </div>
                        <div id="errors" style="color: red;"></div>
                        
                        <form method="post" id="change_password_form">
                        {{csrf_field()}}
                          <div class="modal-body">
                            <label>Old Passwod: </label>
                            <div class="form-group">
                                <input type="password" placeholder="Enter Old Password" class="form-control" name="oldpassword" id="oldpassword">
                            </div>

                            <label>New Password: </label>
                            <div class="form-group">
                                <input type="password" placeholder="Enter New Password" class="form-control" name="newpassword" id="newpassword">
                            </div>

                            <label>Confirm Password: </label>
                            <div class="form-group">
                                <input type="password" placeholder="Enter Confirm Password" class="form-control" name="confirmpassword" id="confirmpassword">
                            </div>

                          </div>
                          <div class="modal-footer">
                            <input type="reset" class="btn btn-outline-secondary btn-lg" data-dismiss="modal"
                            value="close">
                            <input type="button" class="btn btn-outline-primary btn-lg" onclick="changePassword()"  value="Submit">
                          </div>
                        </form>
                      </div>
                    </div>
                  </div>

                  <!-- Modal Settings-->
                  <div class="modal fade text-left" id="Selltings" tabindex="-1" role="dialog" aria-labelledby="RditProduct"
                  aria-hidden="true">
                    <div class="modal-dialog" role="document">
                      <div class="modal-content">
                        <div class="modal-header">
                          <label class="modal-title text-text-bold-600" id="RditProduct">Setting</label>
                          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                          </button>
                        </div>
                        <div id="errors" style="color: red;"></div>
                        
                        <form method="post" id="settings">
                        {{csrf_field()}}
                          <div class="modal-body">

                            <label>Set Currency: </label>
                            <div class="form-group">
                                <input type="text" placeholder="Enter your currency sign" value="{{{Auth::user()->currency}}}" class="form-control" name="currency" id="currency">
                            </div>

                            <label>Tax (%): </label>
                            <div class="form-group">
                                <input type="text" placeholder="Enter Tax in percentage (%)" value="{{{Auth::user()->tax}}}" class="form-control" name="tax" id="tax">
                            </div>

                          </div>
                          <div class="modal-footer">
                            <input type="reset" class="btn btn-outline-secondary btn-lg" data-dismiss="modal"
                            value="close">
                            <input type="button" class="btn btn-outline-primary btn-lg" onclick="settings()"  value="Submit">
                          </div>
                        </form>
                      </div>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>

    </div>
    <!-- /#wrapper -->

    @include('theme.script')

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.19.1/jquery.validate.min.js"></script>
    <script type="text/javascript">
        function changePassword(){     
            var oldpassword=$("#oldpassword").val();
            var newpassword=$("#newpassword").val();
            var confirmpassword=$("#confirmpassword").val();
            var CSRF_TOKEN = $('input[name="_token"]').val();
            
            if($("#change_password_form").valid()) {
                // $('#loaderimg').show();
                $.ajax({
                    headers: {
                        'X-CSRF-Token': CSRF_TOKEN 
                    },
                    url:"{{ url('admin/changePassword') }}",
                    method:'POST',
                    data:{'oldpassword':oldpassword,'newpassword':newpassword,'confirmpassword':confirmpassword},
                    dataType:"json",
                    beforeSend: function() {
                      $("#loading-image").show();
                    },
                    success:function(data){
                        $("#loading-image").hide();
                        if(data.error.length > 0)
                        {
                            var error_html = '';
                            for(var count = 0; count < data.error.length; count++)
                            {
                                error_html += '<div class="alert alert-danger mt-1">'+data.error[count]+'</div>';
                            }
                            $('#errors').html(error_html);
                            setTimeout(function(){
                                $('#errors').html('');
                            }, 10000);
                        }
                        else
                        {
                            location.reload();
                        }
                    },error:function(data){
                       
                    }
                });
            }
        }

        function settings(){     
            var currency=$("#currency").val();
            var tax=$("#tax").val();
            var CSRF_TOKEN = $('input[name="_token"]').val();
            
            if($("#settings").valid()) {
                // $('#loaderimg').show();
                $.ajax({
                    headers: {
                        'X-CSRF-Token': CSRF_TOKEN 
                    },
                    url:"{{ url('admin/settings') }}",
                    method:'POST',
                    data:{'currency':currency,'tax':tax},
                    dataType:"json",
                    beforeSend: function() {
                      $("#loading-image").show();
                    },
                    success:function(data){
                        $("#loading-image").hide();
                        if(data.error.length > 0)
                        {
                            var error_html = '';
                            for(var count = 0; count < data.error.length; count++)
                            {
                                error_html += '<div class="alert alert-danger mt-1">'+data.error[count]+'</div>';
                            }
                            $('#errors').html(error_html);
                            setTimeout(function(){
                                $('#errors').html('');
                            }, 10000);
                        }
                        else
                        {
                            location.reload();
                        }
                    },error:function(data){
                       
                    }
                });
            }
        }

        $(document).ready(function() {
            $( "#settings" ).validate({
                rules :{
                    currency:{
                        required: true
                    },
                    tax: {
                        required: true,
                    },                    
                },

            });        
        });

        $(document).ready(function() {
            $( "#change_password_form" ).validate({
                rules :{
                    oldpassword:{
                        required: true,
                        minlength:6
                    },
                    newpassword: {
                        required: true,
                        minlength:6,
                        maxlength:12,

                    },
                    confirmpassword: {
                        required: true,
                        equalTo: "#newpassword",
                        minlength:6,

                    },
                    
                },

            });        
        });
    </script>

</body>

</html>