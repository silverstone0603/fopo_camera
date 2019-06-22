package com.teamfopo.fopo;

import android.util.Log;
import com.teamfopo.fopo.nodes.MarkerMyItems;

public class JavaClassTest {
    public PhotozoneDTO[] printPhotozoneList() {
        PhotozoneDTO[] PhotozoneDTO = null;

        modPhotoProcess photoProcess = new modPhotoProcess();
        modPhotoProcess.listPhotoZone listPhotoZone = photoProcess.new listPhotoZone();

        try {
            PhotozoneDTO = listPhotoZone.execute().get();
            System.out.println("포토존 목록");

            for (int i = 0; i < PhotozoneDTO.length; i++) {
                System.out.println("---------------------------------------------");
                System.out.println("포토존 번호: " + PhotozoneDTO[i].getZone_no()); // Int
                System.out.println("포토존 이름: " + PhotozoneDTO[i].getZone_placename()); // String
                System.out.println("포토존 위도: " + PhotozoneDTO[i].getZone_lat()); // Double
                System.out.println("포토존 경도: " + PhotozoneDTO[i].getZone_lng()); // Double
            }
            return PhotozoneDTO;
        } catch (Exception e) {
            Log.d("printList Method Error", e.toString());
        }
        return PhotozoneDTO;
    }
}