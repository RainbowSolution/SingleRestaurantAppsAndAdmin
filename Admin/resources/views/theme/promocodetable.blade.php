<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>Offer Name</th>
            <th>Offer Code</th>
            <th>Offer in percentage (%) </th>
            <th>Description </th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        foreach ($getpromocode as $promocode) {
        ?>
        <tr id="dataid{{$promocode->id}}">
            <td>{{$promocode->id}}</td>
            <td>{{$promocode->offer_name}}</td>
            <td>{{$promocode->offer_code}}</td>
            <td>{{$promocode->offer_amount}}</td>
            <td>{{$promocode->description}}</td>
            <td>{{$promocode->created_at}}</td>
            <td>
                <span>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="GetData('{{$promocode->id}}')" title="" data-original-title="Edit">
                        <i class="fa fa-pencil color-muted m-r-5"></i> 
                    </a>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="DeleteData('{{$promocode->id}}')" title="" data-original-title="Delete">
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