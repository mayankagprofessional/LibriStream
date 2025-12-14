package info.mayankag.UserProfileService.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address}") String serverAddress,
            @Value("${billing.service.grpc.port}") int serverPort
    ){
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String userId,
                                                String firstname,
                                                String lastname,
                                                String email){

        BillingRequest request = BillingRequest
                .newBuilder()
                .setUserId(userId)
                .setFirstName(firstname)
                .setLastName(lastname)
                .setEmail(email)
                .build();

        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from Billing Service via GRPC: {}", response);

        return response;
    }

}
