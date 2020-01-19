package com.datacube;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Dale
 * @create 2020-01-03 14:54
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class BISPServerControllerTest {

    @Test
    public void createProjectTest(){

        String url = "http://192.168.1.7:8084/bi/project/create";

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("project_name", "Test01");
        params.add("project_name", 2);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(params, null),
                String.class);

        System.out.println("result: "+response.getBody());
    }

    @Test
    public void databaseSourceSaveTest(){

        String url = "http://192.168.1.7:8084/bi/project/db/data";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", "Test02");
        params.add("dialect", "mysql");
        params.add("database", "dccp");
        params.add("host", "192.168.1.7");
        params.add("port", "3306");
        params.add("user", "root");
        params.add("password", "root");
        params.add("sql", "select * from database_source");


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(params, null),
                String.class);

        System.out.println("result: "+response.getBody());
    }


    @Test
    public void dataPreviewTest(){

        String url = "http://192.168.1.7:8084/bi/project/preview";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", "BiTest");
        params.add("file_id", "123");
        params.add("limit", "1");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(params,null),
                String.class);

        System.out.println("result: "+response.getBody());
    }


    @Test
    public void getAllDataTest(){

        String url = "http://192.168.1.7:8084/bi/project/db/alldata";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", "bitest");
        params.add("file_id", "123");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(params,null),
                String.class);

        System.out.println("result: "+response.getBody());
    }


    @Test
    public void getFeatureListTest(){

        String url = "http://192.168.1.7:8084/bi/feature/list";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("project_id", "bitest");
        params.add("feature_type", "NUM");
        //params.add("feature_type", "CAT");
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(params,null),
                String.class);

        System.out.println("result: "+response.getBody());
    }


    @Test
    public void workSheetFeatureTest(){

        String url = "http://192.168.1.7:8084/bi/worksheet/features";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();


        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>("\n" +
                        "{\n" +
                        "\t\"project_id\": \"BiTest\",\n" +
                        "\t\"worksheet_id\": \"d477df361a3311ea9660d89ef3787a2c\",\n" +
                        "\t\"features\": {\n" +
                        "\t\t\"x\": [{\n" +
                        "\t\t\t\"name\": \"Pclass\"\n" +
                        "\t\t}],\n" +
                        "\t\t\"y\": [{\n" +
                        "\t\t\t\"feature\": {\n" +
                        "\t\t\t\t\"name\": \"Age\",\n" +
                        "\t\t\t\t\"legend\": \"CountD\",\n" +
                        "\t\t\t\t\"color\": {\n" +
                        "\t\t\t\t\t\"name\": \"Embarked\"\n" +
                        "\t\t\t\t},\n" +
                        "\t\t\t\t\"labels\": [{\n" +
                        "\t\t\t\t\t\"name\": \"Parch\",\n" +
                        "\t\t\t\t\t\"legend\": \"SUM\"\n" +
                        "\t\t\t\t}, {\n" +
                        "\t\t\t\t\t\"name\": \"Embarked\"\n" +
                        "\t\t\t\t}]\n" +
                        "\t\t\t}\n" +
                        "\t\t}, {\n" +
                        "\t\t\t\"feature\": {\n" +
                        "\t\t\t\t\"name\": \"Sex\"\n" +
                        "\t\t\t}\n" +
                        "\t\t}]\n" +
                        "\t}\n" +
                        "}"),
                String.class);

        System.out.println("result: "+response.getBody());
    }


}
