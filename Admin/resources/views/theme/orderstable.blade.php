<table class="table table-striped table-bordered zero-configuration">
    <thead>
        <tr>
            <th>#</th>
            <th>User Name</th>
            <th>Order Number</th>
            <th>Address</th>
            <th>Payment Type</th>
            <th>Payment ID</th>
            <th>Order Status</th>
            <th>Created at</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <?php
        foreach ($getorders as $orders) {
        ?>
        <tr id="dataid{{$orders->id}}">
            <td>{{$orders->id}}</td>
            <td>{{$orders['users']->name}}</td>
            <td>{{$orders->order_number}}</td>
            <td>{{$orders->address}}</td>
            <td>
                @if($orders->payment_type =='0')
                      COD
                @else
                      Online
                @endif
            </td>
            <td>
                @if($orders->razorpay_payment_id == '')
                    --
                @else
                    {{$orders->razorpay_payment_id}}
                @endif
            </td>
            <td>
                @if($orders->status == '1')
                    Order Received
                @elseif ($orders->status == '2')
                    On the way
                @elseif ($orders->status == '3')
                    Delivered
                @else
                    Cancelled
                @endif
            </td>
            <td>{{$orders->created_at}}</td>
            <td>
                <span>
                    <a href="#" data-toggle="tooltip" data-placement="top" onclick="DeleteData('{{$orders->id}}')" title="" data-original-title="Delete">
                        <span class="badge badge-danger">Delete</span>
                    </a>
                    <a data-toggle="tooltip" href="{{URL::to('admin/invoice/'.$orders->id)}}" data-original-title="View">
                        <span class="badge badge-warning">View</span>
                    </a>

                    @if($orders->status == '1')
                        <a ddata-toggle="tooltip" data-placement="top" onclick="StatusUpdate('{{$orders->id}}','2')" title="" data-original-title="Order Received">
                            <span class="badge badge-secondary px-2" style="color: #fff;">Order Received</span>
                        </a>
                    @elseif ($orders->status == '2')
                        <a ddata-toggle="tooltip" data-placement="top" onclick="StatusUpdate('{{$orders->id}}','3')" title="" data-original-title="Out for Delivery">
                            <span class="badge badge-light px-2" style="color: #fff;">Out for Delivery</span>
                        </a>
                    @elseif ($orders->status == '3')
                        <a ddata-toggle="tooltip" data-placement="top" title="" data-original-title="Out for Delivery">
                            <span class="badge badge-success px-2" style="color: #fff;">Delivered</span>
                        </a>
                    @else
                        <span class="badge badge-danger px-2">Cancelled</span>
                    @endif
                </span>
            </td>
        </tr>
        <?php
        }
        ?>
    </tbody>
</table>