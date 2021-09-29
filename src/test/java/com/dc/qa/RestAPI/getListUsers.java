package com.dc.qa.RestAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class getListUsers {

	public static Workbook wk;
	public static  Sheet sh;
	public static String fpath ="D:/Sandeep.xlsx";	
	
	@Test(priority =1)
	public void getListofUsers()
	{
		//Base URI
		RestAssured.baseURI="https://reqres.in/api/Users";

/*  how to pass basic authentication - username & passowrd before creating request object
		
		PreemptiveBasicAuthScheme suath = new PreemptiveBasicAuthScheme();
		
		suath.getUserName();
		suath.getPassword();
		RestAssured.authentication= suath;
*/		
		
		
		//Request Object
		RequestSpecification httprequest = RestAssured.given();

		//Response Object
		Response response =httprequest.request(Method.GET,"?page=2");

		//String resposneBody =response.getBody().asString();

		//Validating Response Status Code
		int responsStatusCode= response.getStatusCode();
		System.out.println("response Status code is:"+responsStatusCode);
		Assert.assertEquals(responsStatusCode, 200);

		//Extracting total no of pages value from the response
		int strTotalPages =response.jsonPath().get("total_pages");
		System.out.println("Total number of pages:"+strTotalPages);		

	}


	@Test(priority=2,dataProvider ="getData")
	public void postCreateUsers(String strEmpName, String Job)
	{
		//baseuRI
		RestAssured.baseURI = "https://reqres.in/";

		//request object
		RequestSpecification httRequest = RestAssured.given();

		//creating a requesting payload
		JSONObject obj = new JSONObject();
		obj.put("name", strEmpName);
		obj.put("job", Job);

		httRequest.header("content-type","application/json");
		httRequest.body(obj.toJSONString());

		//response object
		Response response =httRequest.request(Method.POST,"/api/users");

		int StatusCode =response.getStatusCode();
		System.out.println("status of the creation request:" +StatusCode);
		Assert.assertEquals(StatusCode, 201);

       String userid =response.jsonPath().get("id");
       System.out.println("user id of the newly created user:" +userid);
       
       String strCreatedDate= response.jsonPath().get("createdAt");
       System.out.println("created Date is: " +strCreatedDate);


	}
	
@Test(priority =3)
public void putUser()
{
	RestAssured.baseURI ="https://reqres.in";
	
	//request object
	RequestSpecification httpPutRequest = RestAssured.given();
	
	//Json Object - Requesting Payload
	JSONObject objPut = new JSONObject();
	objPut.put("name", "morpheus");
	objPut.put("job1", "zion resident");
	
	httpPutRequest.body(objPut.toJSONString());
	
	//resoponse Object
	Response resp = httpPutRequest.request(Method.PUT, "/api/users/2");
	
	//validating status code
	int putStatusCode = resp.getStatusCode();
	System.out.println("put request Status code:"+putStatusCode);
	Assert.assertEquals(putStatusCode, 200);
	
	
	String strUpdatedDate =resp.jsonPath().get("updatedAt");
	System.out.println("Updated Date is :" + strUpdatedDate);
	
}

@Test(priority =4)

public void deleteUsers() {
	
	RestAssured.baseURI ="https://reqres.in";
	
	//request object
	RequestSpecification req = RestAssured.given();
	
	//response object
	Response resp = req.request(Method.DELETE,"/api/users/2");
	
	int strDeleteStsCd =resp.getStatusCode();
	System.out.println("Delete Request status code: "+resp.getStatusCode());
	Assert.assertEquals(strDeleteStsCd, 204);
	
}



	@DataProvider(name="getData")
	public static Object[][] getData(){
	
		
		try {
			FileInputStream file = new FileInputStream(fpath);
			try {
				wk = WorkbookFactory.create(file);
				} catch (EncryptedDocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sh = wk.getSheet("Test");
		int strRow =sh.getLastRowNum();
		int strcol =sh.getRow(0).getLastCellNum();
	
		Object[][] data = new Object[strRow][strcol];
	
			
		for(int i=0;i<strRow;i++)
		{
			for(int j=0;j<strcol;j++)
			{
				data[i][j]=sh.getRow(i+1).getCell(j).toString();
				System.out.println("value is:"+data[i][j]);
			}
		}
		return data;
	}


}
