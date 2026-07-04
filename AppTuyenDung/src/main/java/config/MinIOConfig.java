package config;

import io.minio.MinioClient;

// thư mục other-project
public class MinIOConfig {
        private static final MinioClient CLIENT = MinioClient.builder().endpoint("http://s3.103.216.117.40.nip.io").credentials("scontract","scontract@2025").build();

        public  static MinioClient getClient(){
                return CLIENT;
        }
}
