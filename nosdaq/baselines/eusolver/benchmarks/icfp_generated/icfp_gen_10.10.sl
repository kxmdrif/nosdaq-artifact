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
(constraint (= (f #xA8BBA2529038B2AF) #xA8A222529020A2A8))
(constraint (= (f #x240C1B63D28B2BB9) #x24081242128A2A21))
(constraint (= (f #x0F29BD9D9F98EE13) #x0829211110108812))
(constraint (= (f #xF6D5697B421AC65B) #x8495494242128452))
(constraint (= (f #x86D8FC39EC91DC17) #x8490802108911014))
(constraint (= (f #x2F24A006527A4829) #x8D6DE012F76ED87B))
(constraint (= (f #x48C488A8D1BA8073) #xDA4D99FA752F8159))
(constraint (= (f #x87AA488208BBEBAB) #x96FED9861A33C301))
(constraint (= (f #x0685234004E11711) #x138F69C00EA34533))
(constraint (= (f #xACC88808B93E0A11) #x0659981A2BBA1E33))
(constraint (= (f #xD9B3DAAAFBF56FE2) #x8D1B9000F3E04FA6))
(constraint (= (f #xD67598938A6EBF38) #x8360C9BA9F4C3DA8))
(constraint (= (f #xD7909A24B8B7F13C) #x86B1CE6E2A27D3B4))
(constraint (= (f #x3E356D5067DD3B44) #xBAA047F13797B1CC))
(constraint (= (f #x9451193A2F625DAC) #xBCF34BAE8E271904))
(constraint (= (f #xF4C347D61995C39B) #x8482441411150212))
(constraint (= (f #x230CFA09963F3395) #x2208820914202215))
(constraint (= (f #x0B0278E3CADA9DFB) #x0A0240820A929102))
(constraint (= (f #x09B2CD7245DAAF81) #x092289424512A801))
(constraint (= (f #x412CD1A6F14A62B8) #xC38674F4D3DF2828))
(constraint (= (f #x96ED9EC1FFE8756C) #xC4C8DC45FFB96044))
(constraint (= (f #x7C8E338DA9E8E6D6) #x75AA9AA8FDBAB482))
(constraint (= (f #x73076ABADA4AC31E) #x591640308EE0495A))
(constraint (= (f #xF067AEB2F0FBB55D) #x804428A280822551))
(constraint (= (f #x2B9C4BC03EF9468C) #x82D4E340BCEBD3A4))
(constraint (= (f #x99493331A2206043) #xCBDB9994E66120C9))
(constraint (= (f #x85C72851043D771D) #x915578F30CB86557))
(constraint (= (f #xCC5F714C0D047A6D) #x8850414809044249))
(constraint (= (f #x71A68F0FEFDCF39B) #x4124880808108212))
(constraint (= (f #xE678A70B450AEAC1) #x8440A40A450A8A81))
(constraint (= (f #x3C0FCAD6319D8F07) #x20080A9421110804))
(constraint (= (f #x6E26200A44AF2543) #x4A72601ECE0D6FC9))
(constraint (= (f #x37C185B22E9165B9) #x2401052228914521))
(constraint (= (f #x030E5BA20C226199) #x092B12E6246724CB))
(constraint (= (f #xDC06D120EC394ED7) #x9004912088214894))
(constraint (= (f #xE93D197852DF109F) #x8921114052901090))
(constraint (= (f #x9EC8850750143BED) #x9088850450142209))
(constraint (= (f #x2CA4EAB7EAE7C187) #x28A48AA40A840104))
(constraint (= (f #x947177C86AD86919) #x944144084A904911))
(constraint (= (f #x61BCFE23CE5C359B) #x4120802208502512))
(constraint (= (f #xE710B31541513743) #x8410A21541512442))
(check-synth)
