@extends('theme.default')

@section('content')
<style type="text/css">
    .pac-container {
    z-index: 10000 !important;
}
</style>
<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Item</a></li>
        </ol>
        <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#addProduct" data-whatever="@addProduct">Add Item</button>

        <!-- Add Item -->
        <div class="modal fade" id="addProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Add New Item</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    
                    <form id="add_product" enctype="multipart/form-data">
                    <div class="modal-body">
                        <span id="msg"></span>
                        @csrf
                        <div class="form-group">
                            <label for="cat_id" class="col-form-label">Category:</label>
                            <select name="cat_id" class="form-control" id="cat_id">
                                <option value="">Select Category</option>
                                <?php
                                foreach ($getcategory as $category) {
                                ?>
                                <option value="{{$category->id}}">{{$category->category_name}}</option>
                                <?php
                                }
                                ?>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="item_name" class="col-form-label">Item Name:</label>
                            <input type="text" class="form-control" name="item_name" id="item_name" placeholder="Item Name">
                        </div>
                        <div class="form-group">
                            <label for="price" class="col-form-label">Price:</label>
                            <input type="text" class="form-control" name="price" id="price" placeholder="Price">
                        </div>
                        <div class="form-group">
                            <label for="delivery_time" class="col-form-label">Delivery time:</label>
                            <input type="text" class="form-control" name="delivery_time" id="delivery_time" placeholder="Price">
                        </div>
                        <div class="form-group">
                            <label for="getprice" class="col-form-label">Description:</label>
                            <textarea class="form-control" rows="10" name="description" id="description" placeholder="Product Description"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="colour" class="col-form-label">Select Item images:</label>
                            <input type="file" multiple="true" class="form-control" name="file[]" id="file">
                            <input type="hidden" name="removeimg" id="removeimg">
                        </div>
                        <div class="gallery"></div>

                        <div class="form-group">
                            <label for="colour" class="col-form-label">Select Ingredients images:</label>
                            <input type="file" multiple="true" class="form-control" name="ingredients[]" id="ingredients">
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

        <!-- Edit Item -->
        <div class="modal fade" id="EditProduct" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabeledit" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <form method="post" name="editproduct" class="editproduct" id="editproduct" enctype="multipart/form-data">
                    @csrf
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="exampleModalLabeledit">Edit Item</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <span id="emsg"></span>
                        <div class="modal-body">
                            <input type="hidden" class="form-control" id="id" name="id">
                            <div class="form-group">
                                <label for="getcat_id" class="col-form-label">Category:</label>
                                <select name="getcat_id" class="form-control" id="getcat_id">
                                    <option value="">Select Category</option>
                                    <?php
                                    foreach ($getcategory as $category) {
                                    ?>
                                    <option value="{{$category->id}}">{{$category->category_name}}</option>
                                    <?php
                                    }
                                    ?>
                                </select>
                            </div>
                            <div class="form-group">
                                <label for="getitem_name" class="col-form-label">Item Name:</label>
                                <input type="text" class="form-control" id="getitem_name" name="item_name" placeholder="Item Name">
                            </div>
                            <div class="form-group">
                                <label for="getprice" class="col-form-label">Price:</label>
                                <input type="text" class="form-control" name="getprice" id="getprice" placeholder="Price">
                            </div>
                            <div class="form-group">
                                <label for="getdelivery_time" class="col-form-label">Delivery time:</label>
                                <input type="text" class="form-control" name="getdelivery_time" id="getdelivery_time" placeholder="Price">
                            </div>
                            <div class="form-group">
                                <label for="getprice" class="col-form-label">Description:</label>
                                <textarea class="form-control" rows="10" name="getdescription" id="getdescription" placeholder="Product Description"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btna-secondary" data-dismiss="modal">Close</button>
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
                    <h4 class="card-title">All Item</h4>
                    <div class="table-responsive" id="table-display">
                        @include('theme.itemtable')
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
     
    $('#add_product').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        form_data.append('file',$('#file')[0].files);
        form_data.append('ingredients',$('#ingredients')[0].files);
        $.ajax({
            url:"{{ URL::to('admin/item/store') }}",
            method:"POST",
            data:form_data,
            cache: false,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function(result) {
                var msg = '';
                $('div.gallery').html('');  
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
                    ProductTable();
                    $('#message').html(msg);
                    $("#addProduct").modal('hide');
                    $("#add_product")[0].reset();
                    setTimeout(function(){
                      $('#message').html('');
                    }, 5000);
                }
            },
        })
    });

    $('#editproduct').on('submit', function(event){
        event.preventDefault();
        var form_data = new FormData(this);
        $.ajax({
            url:"{{ URL::to('admin/item/update') }}",
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
                    ProductTable();
                    $('#message').html(msg);
                    $("#EditProduct").modal('hide');
                    $("#editproduct")[0].reset();
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
        url:"{{ URL::to('admin/item/show') }}",
        data: {
            id: id
        },
        method: 'POST', //Post method,
        dataType: 'json',
        success: function(response) {
            jQuery("#EditProduct").modal('show');
            $('#id').val(response.ResponseData.id);
            $('#getcat_id').val(response.ResponseData.cat_id);
            $('#getitem_name').val(response.ResponseData.item_name);
            $('#getprice').val(response.ResponseData.item_price);
            $('#getdelivery_time').val(response.ResponseData.delivery_time);
            $('#getdescription').val(response.ResponseData.item_description);
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
        text: "Do you want to delete this product?",
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
                url:"{{ URL::to('admin/item/destroy') }}",
                data: {
                    id: id
                },
                method: 'POST',
                success: function(response) {
                    if (response == 1) {
                        swal({
                            title: "Approved!",
                            text: "Item has been deleted.",
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
function ProductTable() {
    $.ajax({
        url:"{{ URL::to('admin/item/list') }}",
        method:'get',
        success:function(data){
            $('#table-display').html(data);
            $(".zero-configuration").DataTable()
        }
    });
}
 $(document).ready(function() {
     var imagesPreview = function(input, placeToInsertImagePreview) {
          if (input.files) {
              var filesAmount = input.files.length;
              $('div.gallery').html('');
              var n=0;
              for (i = 0; i < filesAmount; i++) {
                  var reader = new FileReader();
                  reader.onload = function(event) {
                       $($.parseHTML('<div>')).attr('class', 'imgdiv').attr('id','img_'+n).html('<img src="'+event.target.result+'" class="img-fluid"><span id="remove_"'+n+' onclick="removeimg('+n+')">&#x2716;</span>').appendTo(placeToInsertImagePreview); 
                      n++;
                  }
                  reader.readAsDataURL(input.files[i]);                                  
             }
          }
      };

     $('#file').on('change', function() {
         imagesPreview(this, 'div.gallery');
     });
 
});
var images = [];
function removeimg(id){
    images.push(id);
    $("#img_"+id).remove();
    $('#remove_'+id).remove();
    $('#removeimg').val(images.join(","));
}
</script>
@endsection