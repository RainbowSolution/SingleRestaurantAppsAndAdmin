<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Ratting</th>
            <th>Comment</th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        $i=1;
        foreach ($getreview as $reviews) {
        ?>
        <tr id="dataid{{$reviews->id}}">
            <td>{{$i}}</td>
            <td>{{$reviews['users']->name}}</td>
            <td><i class="fa fa-star"></i> {{$reviews->ratting}}</td>
            <td>{{$reviews->comment}}</td>
            <td>{{$reviews->created_at}}</td>
            <td>
                <span>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="DeleteData('{{$reviews->id}}')" title="" data-original-title="Delete">
                                        <span class="badge badge-danger">Delete</span>
                                    </a>
                </span>
            </td>
        </tr>
        <?php
        $i++;
        }
        ?>
    </tbody>
</table>