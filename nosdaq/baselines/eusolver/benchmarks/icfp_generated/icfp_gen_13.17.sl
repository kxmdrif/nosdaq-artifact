(set-logic BV)

(define-fun shr1 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000001))
(define-fun shr4 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000004))
(define-fun shr16 ((x (BitVec 64))) (BitVec 64) (bvlshr x #x0000000000000010))
(define-fun shl1 ((x (BitVec 64))) (BitVec 64) (bvshl x #x0000000000000001))
(define-fun if0 ((x (BitVec 64)) (y (BitVec 64)) (z (BitVec 64))) (BitVec 64) (ite (= x #x0000000000000001) y z))

(synth-fun f ( (x (BitVec 64))) (BitVec 64)
(

(Start (BitVec 64) (#x0000000000000000 #x0000000000000001 x (bvnot Start)
                    (shl1 Start)
 		    (shr1 Start)
		    (shr4 Start)
		    (shr16 Start)
		    (bvand Start Start)
		    (bvor Start Start)
		    (bvxor Start Start)
		    (bvadd Start Start)
		    (if0 Start Start Start)
 ))
)
)
(constraint (= (f #xFDAD0566E69CC0E0) #x0252FA9919633F1F))
(constraint (= (f #x1010AB2E77646F14) #xEFEF54D1889B90EB))
(constraint (= (f #x09793F55BD6086B4) #xF686C0AA429F794B))
(constraint (= (f #xFAE3C33BFFC8D0D8) #x051C3CC400372F27))
(constraint (= (f #x88AA4621071037E0) #x7755B9DEF8EFC81F))
(constraint (= (f #x5A2BE8906E6632BC) #xA5D4176F9199CD43))
(constraint (= (f #x3B03D71DDBF2F35D) #xC4FC28E2240D0CA2))
(constraint (= (f #x2249F77BBD6ED17C) #xDDB6088442912E83))
(constraint (= (f #x48D0E02506F669AE) #xB72F1FDAF9099651))
(constraint (= (f #xBE50BF92A9A282D9) #x41AF406D565D7D26))
(constraint (= (f #x5A0D42C216650E0C) #xB41A85842CCA1C19))
(constraint (= (f #xB27647ACB089B945) #x64EC8F596113728A))
(constraint (= (f #x83E9CAEF45A1A4FD) #x07D395DE8B4349FA))
(constraint (= (f #x69C81ACBD6D583FC) #xD3903597ADAB07F9))
(constraint (= (f #x48839AF17A711A04) #x910735E2F4E23409))
(constraint (= (f #x0000000000047B69) #xFFFFFFFFFFFB8496))
(constraint (= (f #x000000000004133E) #xFFFFFFFFFFFBECC1))
(constraint (= (f #x0000000000043044) #xFFFFFFFFFFFBCFBB))
(constraint (= (f #x000000000004F8EC) #xFFFFFFFFFFFB0713))
(constraint (= (f #x000000000004620A) #xFFFFFFFFFFFB9DF5))
(constraint (= (f #x491FA36597036710) #x923F46CB2E06CE21))
(constraint (= (f #xFA92F0739E1B0757) #xF525E0E73C360EAE))
(constraint (= (f #x1ED6037896D7DA35) #x3DAC06F12DAFB46A))
(constraint (= (f #xDE26F21AEAF3AF15) #xBC4DE435D5E75E2A))
(constraint (= (f #x18604ED1611F104F) #x30C09DA2C23E209E))
(constraint (= (f #x000000000006C52C) #xFFFFFFFFFFF93AD3))
(constraint (= (f #x0000000000066813) #xFFFFFFFFFFF997EC))
(constraint (= (f #x00000000000676EB) #xFFFFFFFFFFF98914))
(constraint (= (f #x000000000006CAE6) #xFFFFFFFFFFF93519))
(constraint (= (f #x000000000006CC4C) #xFFFFFFFFFFF933B3))
(constraint (= (f #x000000000005CF77) #x000000000005CF78))
(constraint (= (f #x0000000000051FC5) #x0000000000051FC6))
(constraint (= (f #x000000000005FCA9) #x000000000005FCAA))
(constraint (= (f #x000000000005723D) #x000000000005723E))
(constraint (= (f #x0000000000053B21) #x0000000000053B22))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x000000000007FB9C) #x000000000007FB9D))
(constraint (= (f #x000000000007C4C7) #x000000000007C4C8))
(constraint (= (f #x000000000007A4BF) #x000000000007A4C0))
(constraint (= (f #x0000000000077090) #x0000000000077091))
(constraint (= (f #x000000000007A3E0) #x000000000007A3E1))
(constraint (= (f #xD5102FB8A1984D12) #x2AEFD0475E67B2ED))
(constraint (= (f #x1D0E625D44B26FD5) #xE2F19DA2BB4D902A))
(constraint (= (f #x94C98D93349AF1F6) #x6B36726CCB650E09))
(constraint (= (f #xCBFD22170C4043B8) #x3402DDE8F3BFBC47))
(constraint (= (f #x864810906B1D07C6) #x0C902120D63A0F8D))
(constraint (= (f #x21C2A08D5507A807) #x4385411AAA0F500E))
(constraint (= (f #xF192E6122B67EF9C) #xE325CC2456CFDF39))
(constraint (= (f #x994608FA710360D3) #x328C11F4E206C1A6))
(constraint (= (f #xFB9DB8931529B221) #xF73B71262A536442))
(constraint (= (f #x1A674446C5401943) #xE598BBB93ABFE6BC))
(constraint (= (f #x000000000005C86D) #x000000000005C86E))
(constraint (= (f #x0000000000043057) #xFFFFFFFFFFFBCFA8))
(constraint (= (f #x000000000006EA48) #xFFFFFFFFFFF915B7))
(constraint (= (f #x0000000000066C65) #xFFFFFFFFFFF9939A))
(constraint (= (f #xFFFFFFFFFFFFFFFE) #xFFFFFFFFFFFFFFFD))
(constraint (= (f #x0000000000076495) #x0000000000076496))
(constraint (= (f #x00000000000744ED) #x00000000000744EE))
(constraint (= (f #x000000000005B69A) #x000000000005B69B))
(constraint (= (f #x0000000000058F1B) #x0000000000058F1C))
(constraint (= (f #x0000000000046B15) #xFFFFFFFFFFFB94EA))
(constraint (= (f #x0000000000053AFF) #x0000000000053B00))
(constraint (= (f #x740CBE550971F211) #xE8197CAA12E3E422))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #xFFFFFFFFFFFFFFFE))
(check-synth)
