package com.sepehr.jblockchain.factory;

import org.junit.jupiter.api.Test;
import org.sepehr.jblockchain.factory.imp.AccountFactoryImp;
import org.sepehr.jblockchain.factory.imp.KeyFactoryImp;
import org.sepehr.jblockchain.factory.imp.RecoveryCodeFactoryImp;
import org.sepehr.jblockchain.sample.Account;

import java.math.BigInteger;
import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyFactoryTest {

    @Test
    void sameKayPairGenerationTest() {
        final String seed = "Hello";
        KeyFactoryImp keyFactoryImp = new KeyFactoryImp();

        KeyPair keyPair1 = keyFactoryImp.generateKeyPair(seed);
        KeyPair keyPair2 = keyFactoryImp.generateKeyPair(seed);

        assertArrayEquals(keyPair1.getPrivate().getEncoded(), keyPair2.getPrivate().getEncoded());
        String hexCode = new BigInteger(1, keyPair1.getPrivate().getEncoded()).toString(16);
        assertEquals(
                "3082025c0201003082023506072a8648ce3804013082022802820101008f7935d9b9aae9bfabed887acf4951b6f32ec59e3baf3718e8eac4961f3efd3606e74351a9c4183339b809e7c2ae1c539ba7475b85d011adb8b47987754984695cac0e8f14b3360828a22ffa27110a3d62a993453409a0fe696c4658f84bdd20819c3709a01057b195adcd00233dba5484b6291f9d648ef883448677979cec04b434a6ac2e75e9985de23db0292fc1118c9ffa9d8181e7338db792b730d7b9e349592f68099872153915ea3d6b8b4653c633458f803b32a4c2e0f27290256e4e3f8a3b0838a1c450e4e18c1a29a37ddf5ea143de4b66ff04903ed5cf1623e158d487c608e97f211cd81dca23cb6e380765f822e342be484c05763939601cd667021d00baf696a68578f7dfdee7fa67c977c785ef32b233bae580c0bcd5695d0282010016a65c58204850704e7502a39757040d34da3a3478c154d4e4a5c02d242ee04f96e61e4bd0904abdac8f37eeb1e09f3182d23c9043cb642f88004160edf9ca09b32076a79c32a627f2473e91879ba2c4e744bd2081544cb55b802c368d1fa83ed489e94e0fa0688e32428a5c78c478c68d0527b71c9a3abb0b0be12c44689639e7d3ce74db101a65aa2b87f64c6826db3ec72f4b5599834bb4edb02f7c90e9a496d3a55d535bebfc45d4f619f63f3dedbb873925c2f224e07731296da887ec1e4748f87efb5fdeb75484316b2232dee553ddaf02112b0d1f02da30973224fe27aeda8b9d4b2922d9ba8be39ed9e103a63c52810bc688b7e2ed4316e1ef17dbde041e021c6314ed05969e2f0367383944f8fd3819c85d319e05fda7d3480c32ff",
                hexCode
        );
        byte[] bytes = new BigInteger(hexCode, 16).toByteArray();
        assertArrayEquals(bytes, keyPair2.getPrivate().getEncoded());

        Account account = new AccountFactoryImp(new KeyFactoryImp(), new RecoveryCodeFactoryImp())
                .buildAccount();

        System.out.println(account.getPrivateKey());
        System.out.println(account.getPublicKey());
        System.out.println(account.getCreatedTimestamp());




    }

}
