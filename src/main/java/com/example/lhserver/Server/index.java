package com.example.lhserver.Server;
import com.example.lhserver.tools.FaceEngineTest;
import com.example.lhserver.tools.FaceEngineTest2;
import com.example.lhserver.tools.XmlJq;
import com.example.lhserver.tools.ZcRl;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class index {

    @CrossOrigin(origins = "*")
    @GetMapping("/GetOrderMsg")
    public String test(String pay) {
        XmlJq p = new XmlJq();
//        String a = ;
        ArrayList arr = p.getXmlJq(pay);
//        System.out.println(arr.get(0));
//        System.out.print(arr.get(1));
        JSONObject object = new JSONObject();
        //string
        try {
            object.put("message", arr.get(0));
            object.put("signature", arr.get(1));
            object.put("order", arr.get(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/Face_get")
    public String Face_gets(String gh) {
        ZcRl fs = new ZcRl();
        return fs.Face_get(gh);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/Face_del")
    public String Face_dels(String ids) {
//        System.out.println(ids);
        ZcRl fs = new ZcRl();
        return fs.Face_del(ids);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/zh1", method = RequestMethod.POST)
    public String logins(@RequestParam Map<String, Object> map) {
        //        人脸库比对
//        System.out.println(map);
        String img = (String) map.get("img");
        FaceEngineTest2 f = new FaceEngineTest2();
//        System.out.print(img);

        String s = f.Face2(img);
//        System.out.print(s);
        return s;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/zh", method = RequestMethod.POST)
    public String login(@RequestParam Map<String, Object> map) {
//        System.out.println(map);
//        人脸图片比对
        String img = (String) map.get("img");
        FaceEngineTest f = new FaceEngineTest();
//        System.out.print(img);

        String s = f.Face(img);
//        System.out.print(s);
        return s;
    }

    @CrossOrigin(origins = "*")
//    @RequestMapping(value = "/zcrl", method = RequestMethod.POST)
    @PostMapping(value = "/zcrl")
//    @PostMapping("/zcrl")
    public String zcrl(@RequestParam Map<String,Object> params) {
//        System.out.println(map);
        String gh= (String) params.get("gh");
        String img = (String) params.get("img");
//
//        System.out.print(params.get("gh"));
//        System.out.print(params.get("img"));
//        return "参数错误";
        if (img != null && gh!= null) {


            ZcRl fs = new ZcRl();
//        System.out.print(img);

            if (fs.Face_set(img, gh)) {

                return "(" + gh + ")注册成功";

            } else {
                return "(" + gh + ")注册失败";

            }
        }else{

            return "参数错误";

        }
//        System.out.print(s);

    }
}



