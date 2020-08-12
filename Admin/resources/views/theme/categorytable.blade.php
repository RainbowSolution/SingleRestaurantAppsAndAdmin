<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Image</th>
            <th>Category Name</th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        foreach ($getcategory as $category) {
        ?>
        <tr id="dataid{{$category->id}}">
            <td>{{$category->id}}</td>
            <td><img src='{!! asset("public/images/category/".$category->image) !!}' class='img-fluid' style='max-height: 50px;'></td>
            <td>{{$category->category_name}}</td>
            <td>{{$category->created_at}}</td>
            <td>
                <span>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$category->id}}')" title="" data-original-title="Edit">
                        <i class="fa fa-pencil color-muted m-r-5"></i> 
                    </a>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="DeleteData('{{$category->id}}')" title="" data-original-title="Delete">
                        <i class="fa fa-close color-danger"></i>
                    </a>
                </span>
            </td>
        </tr>
        <?php
        }
        ?>
    </tbody>
</table>