@extends('theme.default')

@section('content')

<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Promocode</a></li>
        </ol>
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addPromocode" data-whatever="@addPromocode">Add Promocode</button>
        <!-- Add Promocode -->
        <div class="modal fade" id="addPromocode" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Add New Promocode</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    
                    <form id="add_promocode">
                    <div class="modal-body">
                        <span id="msg"></span>
                        @csrf
                        <div class="form-group">
                            <label for="offer_name" class="col-form-label">Offer Name:</label>
                            <input type="text" class="form-control" name="offer_name" id="offer_name">
                        </div>
                        <div class="form-group">
                            <label for="offer_code" class="col-form-label">Offer Code:</label>
                            <input type="text" class="form-control" name="offer_code" id="offer_code">
                        </div>
                        <div class="form-group">
                            <label for="offer_amount" class="col-form-label">Offer in percentage (%):</label>
                            <input type="text" class="form-control" name="offer_amount" id="offer_amount">
                        </div>
                        <div class="form-group">
                            <label for="description" class="col-form-label">Offer Description:</label>
                            <textarea class="form-control" name="description" id="description" placeholder="Offer Description"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Edit Promocode -->
        <div class="modal fade" id="EditPromocode" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabeledit" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <form method="post" name="editpromocode" class="editpromocode" id="editpromocode">
                    @csrf
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabeledit">Edit Promocode</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <span id="emsg"></span>
                        <div class="modal-body">
                            <input type="hidden" class="form-control" id="id" name="id">
                            <div class="form-group">
                                <label for="getoffer_name" class="col-form-label">Offer Name:</label>
                                <input type="text" class="form-control" name="getoffer_name" id="getoffer_name">
                            </div>
                            <div class="form-group">
                                <label for="getoffer_code" class="col-form-label">Offer Code:</label>
                                <input type="text" class="form-control" name="getoffer_code" id="getoffer_code">
                            </div>
                            <div class="form-group">
                                <label for="getoffer_amount" class="col-form-label">Offer in percentage (%):</label>
                                <input type="text" class="form-control" name="getoffer_amount" id="getoffer_amount">
                            </div>
                            <div class="form-group">
                                <label for="get_description" class="col-form-label">Offer Description:</label>
                                <textarea class="form-control" name="get_description" id="get_description" placeholder="Offer Description"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                            <button type="submit" class="btn btn-primary">Update</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <div class="row">
        <div class="col-12">
            <span id="message"></span>
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title">All Promocode</h4>
                    <div class="table-responsive" id="table-display">
                        @include('theme.promocodetable')
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- #/ container -->
@endsection
@section('script')

<script type="text/javascript">
$(document).ready(function() {
     
    $('#add_promocode').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        $.ajax({
            url:"{{ URL::to('admin/promocode/store') }}",
            method:"POST",
            data:form_data,
            cache: false,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function(result) {
                var msg = '';
                if(result.error.length > 0)
                {
                    for(var count = 0; count < result.error.length; count++)
                    {
                        msg += '<div class="alert alert-danger">'+result.error[count]+'</div>';
                    }
                    $('#msg').html(msg);
                    setTimeout(function(){
                      $('#msg').html('');
                    }, 5000);
                }
                else
                {
                    msg += '<div class="alert alert-success mt-1">'+result.success+'</div>';
                    PromocodeTable();
                    $('#message').html(msg);
                    $("#addPromocode").modal('hide');
                    $("#add_promocode")[0].reset();
                    setTimeout(function(){
                      $('#message').html('');
                    }, 5000);
                }
            },
        })
    });

    $('#editpromocode').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        $.ajax({
            url:"{{ URL::to('admin/promocode/update') }}",
            method:'POST',
            data:form_data,
            cache: false,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function(result) {
                var msg = '';
                if(result.error.length > 0)
                {
                    for(var count = 0; count < result.error.length; count++)
                    {
                        msg += '<div class="alert alert-danger">'+result.error[count]+'</div>';
                    }
                    $('#emsg').html(msg);
                    setTimeout(function(){
                      $('#emsg').html('');
                    }, 5000);
                }
                else
                {
                    msg += '<div class="alert alert-success mt-1">'+result.success+'</div>';
                    PromocodeTable();
                    $('#message').html(msg);
                    $("#EditPromocode").modal('hide');
                    $("#editpromocode")[0].reset();
                    setTimeout(function(){
                      $('#message').html('');
                    }, 5000);
                }
            },
        });
    });
});
function GetData(id) {
    $.ajax({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        },
        url:"{{ URL::to('admin/promocode/show') }}",
        data: {
            id: id
        },
        method: 'POST', //Post method,
        dataType: 'json',
        success: function(response) {
            jQuery("#EditPromocode").modal('show');
            $('#id').val(response.ResponseData.id);
            $('#getoffer_name').val(response.ResponseData.offer_name);
            $('#getoffer_code').val(response.ResponseData.offer_code);
            $('#getoffer_amount').val(response.ResponseData.offer_amount);
            $('#get_description').val(response.ResponseData.description);
        },
        error: function(error) {

            // $('#errormsg').show();
        }
    })
}
function DeleteData(id) {
    // dd(id);
    swal({
        title: "Are you sure?",
        text: "Do you want to delete this Promocode?",
        type: "warning",
        showCancelButton: true,
        confirmButtonClass: "btn-danger",
        confirmButtonText: "Yes, delete it!",
        cancelButtonText: "No, cancel plz!",
        closeOnConfirm: false,
        closeOnCancel: false,
        showLoaderOnConfirm: true,
    },
    function(isConfirm) {
        if (isConfirm) {
            $.ajax({
                headers: {
                    'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
                },
                url:"{{ URL::to('admin/promocode/destroy') }}",
                data: {
                    id: id
                },
                method: 'POST',
                success: function(response) {
                    if (response == 1) {
                        swal({
                            title: "Approved!",
                            text: "Promocode has been deleted.",
                            type: "success",
                            showCancelButton: true,
                            confirmButtonClass: "btn-danger",
                            confirmButtonText: "Ok",
                            closeOnConfirm: false,
                            showLoaderOnConfirm: true,
                        },
                        function(isConfirm) {
                            if (isConfirm) {
                                $('#dataid'+id).remove();
                                swal.close();
                                // location.reload();
                            }
                        });
                    } else {
                        swal("Cancelled", "Something Went Wrong :(", "error");
                    }
                },
                error: function(e) {
                    swal("Cancelled", "Something Went Wrong :(", "error");
                }
            });
        } else {
            swal("Cancelled", "Your record is safe :)", "error");
        }
    });
}
function PromocodeTable() {
    $.ajax({
        url:"{{ URL::to('admin/promocode/list') }}",
        method:'get',
        success:function(data){
            $('#table-display').html(data);
            $(".zero-configuration").DataTable()
        }
    });
}
</script>
@endsection