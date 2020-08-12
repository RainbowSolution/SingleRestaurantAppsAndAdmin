@extends('theme.default')

@section('content')
<!-- row -->

<div class="row page-titles mx-0">
    <div class="col p-md-0">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="{{URL::to('/admin/home')}}">Dashboard</a></li>
            <li class="breadcrumb-item active"><a href="javascript:void(0)">Product Photos</a></li>
        </ol>
    </div>
</div>
<!-- row -->

<div class="container-fluid">
    <!-- End Row -->
    <div class="card">
        <div class="card-header">
            Invoice
            <strong>{{$getusers->order_number}}</strong> 
            <span class="float-right"> <strong>Status:</strong> 
                @if($getusers->status == '1')
                    Order Received
                @elseif ($getusers->status == '2')
                    On the way
                @elseif ($getusers->status == '3')
                    Delivered
                @else
                    Cancelled
                @endif
            </span>

        </div>
        <div class="card-body">
            <div class="row mb-4">
                <div class="col-sm-6">
                    <h6 class="mb-3">To:</h6>
                    <div>
                        <strong>{{$getusers['users']->name}}</strong>
                    </div>
                    <div>{{$getusers->address}}</div>
                    <div>Email: {{$getusers['users']->email}}</div>
                    <div>Phone: {{$getusers['users']->mobile}}</div>
                </div>

                <!-- <div class="col-sm-6">
                    <h6 class="mb-3">To:</h6>
                    <div>
                        <strong>Bob Mart</strong>
                    </div>
                    <div>Attn: Daniel Marek</div>
                    <div>43-190 Mikolow, Poland</div>
                    <div>Email: marek@daniel.com</div>
                    <div>Phone: +48 123 456 789</div>
                </div> -->



            </div>

            <div class="table-responsive-sm">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th class="center">#</th>
                            <th>Item</th>
                            <th class="right">Unit Cost</th>
                            <th class="center">Qty</th>
                            <th class="right">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php
                        $i=1;
                        foreach ($getorders as $orders) {
                        ?>
                        <tr>
                            <td class="center">{{$i}}</td>
                            <td class="left strong">{{$orders->item_name}}</td>
                            <td class="left">${{$orders->item_price}}</td>
                            <td class="center">{{$orders->qty}}</td>
                            <td class="right">$<?php echo $orders->qty * $orders->item_price; ?></td>
                        </tr>
                        <?php
                        $i++;
                        }
                        ?>
                    </tbody>
                </table>
            </div>
            <div class="row">
                <div class="col-lg-4 col-sm-5">

                </div>

                <div class="col-lg-4 col-sm-5 ml-auto">
                    <table class="table table-clear">
                        <tbody>
                            <tr>
                                <td class="left">
                                    <strong>Tax</strong> ({{$getusers->tax}})
                                </td>
                                <td class="right">
                                    <strong>${{$getusers->tax_amount}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Delivery Charge</strong>
                                </td>
                                <td class="right">
                                    <strong>${{$getusers->delivery_charge}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Discount</strong> ({{$getusers->promocode}})
                                </td>
                                <td class="right">
                                    <strong>${{$getusers->discount_amount}}</strong>
                                </td>
                            </tr>
                            <tr>
                                <td class="left">
                                    <strong>Total</strong>
                                </td>
                                <td class="right">
                                    <strong>${{$getusers->order_total}}</strong>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                </div>

            </div>

        </div>
    </div>
    <!-- End Row -->
</div>
<!-- #/ container -->

<!-- #/ container -->
@endsection
@section('script')
@endsection