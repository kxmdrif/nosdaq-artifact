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
(constraint (= (f #x1681FA0BF02970C9) #x2D03F417E052E192))
(constraint (= (f #xBE474F820F473287) #x7C8E9F041E8E650E))
(constraint (= (f #xA7E267B3AFE34AF1) #x4FC4CF675FC695E2))
(constraint (= (f #xA5AFA73C846DE661) #x4B5F4E7908DBCCC2))
(constraint (= (f #x347F08C1B53960C5) #x68FE11836A72C18A))
(constraint (= (f #xAF0529AC16FB4370) #x5E0A53582DF686E0))
(constraint (= (f #x2ABAA7DC6B872E10) #x55754FB8D70E5C20))
(constraint (= (f #x140E78A37635CB62) #x281CF146EC6B96C4))
(constraint (= (f #x0C49593494D978B2) #x1892B26929B2F164))
(constraint (= (f #xC93AEE477511FEF2) #x9275DC8EEA23FDE4))
(constraint (= (f #xF7BA3E0AE6D20F3C) #x00001EF747C15CDA))
(constraint (= (f #x2AE019E3BD26356C) #x0000055C033C77A4))
(constraint (= (f #x9CDA630F8FE47744) #x0000139B4C61F1FC))
(constraint (= (f #xB25B8AE29B186AEA) #x0000164B715C5363))
(constraint (= (f #xFE78AC72D72A8736) #x00001FCF158E5AE5))
(constraint (= (f #x2520E1801405A1C1) #x4A41C300280B4382))
(constraint (= (f #x41424A52400D2085) #x828494A4801A410A))
(constraint (= (f #xC10E1250690D0909) #x821C24A0D21A1212))
(constraint (= (f #x58500401A5830869) #xB0A008034B0610D2))
(constraint (= (f #x0900C0B4860D0787) #x120181690C1A0F0E))
(constraint (= (f #xB212936948C4BD17) #x00001642526D2918))
(constraint (= (f #xE8EA73DCF5F8F633) #x00001D1D4E7B9EBF))
(constraint (= (f #xE37843934F9E0877) #x00001C6F087269F3))
(constraint (= (f #x06D97779A0DA2D7F) #x000000DB2EEF341B))
(constraint (= (f #xD685F7F74E5CA945) #x00001AD0BEFEE9CB))
(constraint (= (f #x2414B006860F0210) #x4829600D0C1E0420))
(constraint (= (f #x814B429252852C00) #x02968524A50A5800))
(constraint (= (f #x1A58300780A5850C) #x34B0600F014B0A18))
(constraint (= (f #xC2D05A5A08070692) #x85A0B4B4100E0D24))
(constraint (= (f #x96038609094B0D00) #x2C070C1212961A00))
(constraint (= (f #x160783403868400E) #x0000000000000000))
(constraint (= (f #x1E09408142484B42) #x0000000000000000))
(constraint (= (f #x528085285038580A) #x0000000000000000))
(constraint (= (f #x382C385A00848006) #x0000000000000000))
(constraint (= (f #xE0684861A582C068) #x0000000000000000))
(constraint (= (f #x0000000000000002) #x0000000000000004))
(constraint (= (f #x0000000000000000) #x0000000000000000))
(constraint (= (f #x960B06858200A00D) #x0000000000000000))
(constraint (= (f #xC2C20C24A052830D) #x0000000000000000))
(constraint (= (f #x96960384A58081A5) #x0000000000000000))
(constraint (= (f #x50A16849424834A5) #x0000000000000000))
(constraint (= (f #x1E1A4B42C1A07861) #x0000000000000000))
(constraint (= (f #x0000000000000003) #x0000000000000000))
(check-synth)
