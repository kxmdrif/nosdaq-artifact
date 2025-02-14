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
(constraint (= (f #xDF874FE99AD588F3) #xDF874FE99AD588F3))
(constraint (= (f #x6269EDEE662F8577) #x6269EDEE662F8577))
(constraint (= (f #x03211EEC6BA7541B) #x03211EEC6BA7541B))
(constraint (= (f #x93135124A95C647F) #x93135124A95C647F))
(constraint (= (f #x090D9E111BD9DD95) #x090D9E111BD9DD95))
(constraint (= (f #xB1F89608A9F07C8A) #xD209BA19FA10859E))
(constraint (= (f #xE32F39A6335CA4B4) #x25714AEA55E5EDDC))
(constraint (= (f #x593DCCE44ACDECC0) #xEB46552CDF563540))
(constraint (= (f #xDB4C4A27AB7A62E8) #x6DD4DE68FD8EA738))
(constraint (= (f #x4E4F40ADEDBD9722) #xD2D1C1F636C6B966))
(constraint (= (f #x000000000000001F) #x000000000000001F))
(constraint (= (f #x0000000000000015) #x0000000000000015))
(constraint (= (f #x0000000000000019) #x0000000000000019))
(constraint (= (f #x0000000000000017) #x0000000000000017))
(constraint (= (f #xB61E359C6A8DB54F) #xDA225EA4BF96DFD1))
(constraint (= (f #xA044D875166E0DE1) #xE0CD689F3AB21623))
(constraint (= (f #x2005F75B7FBB954D) #x600E19ED80CCBFD7))
(constraint (= (f #x95CD6B2BC73F26AF) #xBE57BD7C49416BF1))
(constraint (= (f #xAEDD3D64B209B7E7) #xF36747ADD61AD829))
(constraint (= (f #x00000000001DE8F9) #x000000000026390B))
(constraint (= (f #x00000000001F28B3) #x00000000002179D5))
(constraint (= (f #x00000000001D8833) #x0000000000269855))
(constraint (= (f #x0000000000109A59) #x000000000031AEEB))
(constraint (= (f #x000000000011D7D5) #x000000000032787F))
(constraint (= (f #x000000000000001A) #x0000000000000018))
(constraint (= (f #x0000000000000010) #x0000000000000012))
(constraint (= (f #x0000000000000014) #x0000000000000016))
(constraint (= (f #x000000000000001E) #x000000000000001C))
(constraint (= (f #x00000000001CB7F8) #x000000000025D808))
(constraint (= (f #x000000000016AB30) #x00000000003BFD50))
(constraint (= (f #x00000000001680B0) #x00000000003B81D0))
(constraint (= (f #x000000000013F722) #x0000000000341966))
(constraint (= (f #x0000000000140A28) #x00000000003C1E78))
(constraint (= (f #x000000000018D0C5) #x000000000029714F))
(constraint (= (f #x00000000001A0A61) #x00000000002E1EA3))
(constraint (= (f #x00000000001021CF) #x0000000000306251))
(constraint (= (f #x000000000011CEEB) #x000000000032533D))
(constraint (= (f #x00000000001C5723) #x000000000024F965))
(constraint (= (f #x469523AA813BE33E) #xCBBF64FF834C2542))
(constraint (= (f #xA9D860803A4DA789) #xFA68A1804ED6E89B))
(constraint (= (f #x1649349A54F4F4F8) #x3ADB5DAEFD1D1D08))
(constraint (= (f #xC7AB85744A0666F2) #x48FC8F9CDE0AAB16))
(constraint (= (f #x4BBC33F2E6028EB9) #x4BBC33F2E6028EB9))
(constraint (= (f #xCC403281D43C8FA2) #x54C057827C4590E6))
(constraint (= (f #xD3E68E0F00C77A91) #xD3E68E0F00C77A91))
(constraint (= (f #xA2BDA49FB9E69980) #xE7C6EDA0CA2BAA80))
(constraint (= (f #xF86BB5E62303D776) #x08BCDE2A6504799A))
(constraint (= (f #x56F9BDA1E2B664CA) #xFB0AC6E227DAAD5E))
(constraint (= (f #x000000000000001E) #x000000000000001C))
(constraint (= (f #x0000000000000010) #x0000000000000012))
(constraint (= (f #xB9F07253AC06CD2B) #xCA1096F4F40B577D))
(constraint (= (f #x000000000000001F) #x000000000000001F))
(constraint (= (f #x000000000018CDF7) #x0000000000295619))
(constraint (= (f #x0000000000115404) #x000000000033FC0C))
(constraint (= (f #x000000000011F785) #x000000000032188F))
(constraint (= (f #x66566EB08E7C5DD7) #x66566EB08E7C5DD7))
(constraint (= (f #xDF526D0A729A8BDC) #x61F6B71E97AF9C64))
(constraint (= (f #x000000000000001A) #x0000000000000018))
(constraint (= (f #x000000000000001B) #x000000000000001B))
(check-synth)
