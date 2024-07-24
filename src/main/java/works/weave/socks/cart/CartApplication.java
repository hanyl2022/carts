package works.weave.socks.cart;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnablePrometheusEndpoint
@SpringBootApplication
@RestController
public class CartApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
