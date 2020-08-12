<?php
require_once '../php/Verification.php';
require_once '../php/Validation.php';
require_once '../php/FileWrite.php';
require_once '../php/DbImport.php';
$verification = new Verification();
$validation = new Validation();
$DbImport = new DbImport();
$FileWrite = new FileWrite();
$message = null;
//set the path of files
$path = [
	'sql_path' => '../sql/install.sql',
	'template_path' => '../php/Database.php',
	'output_path' => '../../.env',
	'config_path' => '../../inc/config.php',
];

if (@$_POST['action'] == "add") {
	try
	{
		// Purchase Key Validation
		if (!empty($_POST['userid']) || !empty($_POST['purchase_key'])) {

			$result = $validation->validate($_POST);

			if ($result) {

				$validdata = $verification->verify_purchase($_POST);

				if ($validdata == 'yes') {
					echo json_encode(array('status' => 1, 'msg' => "Verified Successfully"));
				} else {
					echo json_encode(array('status' => 0, 'msg' => $validdata));
					// $message .= $validdata;
				}

			} else {
				echo json_encode(array('status' => 0, 'msg' => $result));
				// $message .= $result;
			}
		}
	} catch (PDOException $e) {
		echo json_encode(array('status' => 0, 'msg' => "Something went wrong please try again.."));
	}
}
if (@$_POST['action'] == "database") {
	try
	{
		//validate all input
		$dbvalid = $validation->run($_POST);
		if ($dbvalid === true) {
			//check install.sql file is exists in sql directory
			if ($validation->checkFileExists($path['sql_path']) == false) {
				$message .= "<li>install.sql file is not exists in sql/ directory!</li>";
			} else {
				//first create the database, then create tables, then write config and database file
				if ($FileWrite->databaseConfig($path, $_POST) === false) {
					//write database file
					echo json_encode(array('status' => 0, 'msg' => "The database could not be created, please chmod inc/database.php file to 777"));
					// $message .= "<li>The database configuration file could not be written, ";
					// $message .= "please chmod inc/database.php file to 777</li>";
				} elseif ($FileWrite->baseUrl($path['config_path']) === false) {
					//write config file
					echo json_encode(array('status' => 0, 'msg' => "The database could not be created, please chmod inc/config.php file to 777"));
					// $message .= "<li>The config  file could not be written, ";
					// $message .= "please chmod inc/config.php file to 777</li>";
				} elseif ($DbImport->createDatabase($_POST) === false) {
					echo json_encode(array('status' => 0, 'msg' => "The database could not be created, please verify your settings."));
					// $message .= "<li>The database could not be created, ";
					// $message .= "please verify your settings.</li>";
				} elseif ($DbImport->createTables($_POST) === false) {
					echo json_encode(array('status' => 0, 'msg' => "The database tables could not be created, please verify your settings."));
					// $message .= "<li>The database tables could not be created, ";
					// $message .= "please verify your settings.</li>";
				} else {
					//redirect to complete installation
					echo json_encode(array('status' => 1, 'msg' => "Success."));

				}
			}

		} else {
			//Display error message
			$message .= $dbvalid;
			echo json_encode(array('status' => 0, 'msg' => $message));
			// $message = $Validation->run($_POST);
		}
	} catch (PDOException $e) {
		echo json_encode(array('status' => 0, 'msg' => "Something went wrong please try again.."));
	}
}

if (@$_POST['action'] == "admindata") {
	try
	{
		// validate Login data
		$lresult = $validation->validate_login($_POST);
		// If validation successful
		if ($lresult === true) {
			// Insert login data into table
			if ($DbImport->insert_login($_POST)) {
				// Redirect to complete app page
				echo json_encode(array('status' => 1, 'msg' => "Success."));
			} else {
				// $_SESSION['message'] = "<li>Failed! Please Try Again</li>";
				echo json_encode(array('status' => 0, 'msg' => "Failed! Please Try Again"));
				// $message .= "<li>Failed! Please Try Again</li>";
				// header('location: index.php?step4=true');
			}
		} else {
			//Display error message
			echo json_encode(array('status' => 0, 'msg' => $lresult));
			// $message .= $lresult;
		}
		// End of login info insert
	} catch (PDOException $e) {
		echo json_encode(array('status' => 0, 'msg' => "Something went wrong please try again.."));
	}
}
?>