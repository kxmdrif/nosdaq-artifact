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
(constraint (= (f #xCAB78395590116A5) #x0000CAB783955901))
(constraint (= (f #x393111390D0FB043) #x0000393111390D0F))
(constraint (= (f #x7BF5B39A881A458E) #x00007BF5B39A881A))
(constraint (= (f #x1A62E96B79C51C0D) #x00001A62E96B79C5))
(constraint (= (f #x90C5BDD0A18273AB) #x000090C5BDD0A182))
(constraint (= (f #x42E40052D3F7809C) #xBD1BFFAD2C087F63))
(constraint (= (f #xEDF17067E5D63857) #x120E8F981A29C7A8))
(constraint (= (f #x174708F2C51970F7) #xE8B8F70D3AE68F08))
(constraint (= (f #x872A83566C6D9236) #x78D57CA993926DC9))
(constraint (= (f #x9463B44D394F97F1) #x6B9C4BB2C6B0680E))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x00000010AB277B62) #x000000000010AB27))
(constraint (= (f #x000000114FEA5C8F) #x0000000000114FEA))
(constraint (= (f #x000000185F09CAC6) #x0000000000185F09))
(constraint (= (f #x8000001EBF0E9D8A) #x00008000001EBF0E))
(constraint (= (f #x8000001771084A09) #x0000800000177108))
(constraint (= (f #xAAAAAAAAAAAAAAB3) #x555555555555554C))
(constraint (= (f #xAAAAAAAAAAAAAABE) #x5555555555555541))
(constraint (= (f #xAAAAAAAAAAAAAABA) #x5555555555555545))
(constraint (= (f #xAAAAAAAAAAAAAAB2) #x555555555555554D))
(constraint (= (f #xAAAAAAAAAAAAAAB5) #x555555555555554A))
(constraint (= (f #x800000166CEE369A) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x0000001A7D94E578) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x0000001D227ED43B) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x8000001577163F50) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x800000159D50B0D3) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x62601FFF43824F8F) #x000062601FFF4382))
(constraint (= (f #x632A8445205DED34) #x9CD57BBADFA212CB))
(constraint (= (f #x0B9A883E5FB4FDD2) #xF46577C1A04B022D))
(constraint (= (f #x9837F2C36F809897) #x67C80D3C907F6768))
(constraint (= (f #x9F43961222BCF623) #x00009F43961222BC))
(constraint (= (f #xBCD460BAFEBF952D) #x0000BCD460BAFEBF))
(constraint (= (f #xDDC65726ED62FE3E) #x2239A8D9129D01C1))
(constraint (= (f #x911A146D119DB229) #x0000911A146D119D))
(constraint (= (f #x67E1E77388C7128C) #x000067E1E77388C7))
(constraint (= (f #xA9AF7848B27C5E35) #x565087B74D83A1CA))
(constraint (= (f #x8000001DECEA6491) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x000000178655FB44) #x0000000000178655))
(constraint (= (f #x80000011B3B5EC7C) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x000000190AE528B5) #xFFFFFFFFFFFFFFFE))
(constraint (= (f #x800000152FCC7D49) #x0000800000152FCC))
(constraint (= (f #xEF4FB54B20C9E674) #x10B04AB4DF36198B))
(constraint (= (f #x485C2BF18DF74231) #xB7A3D40E7208BDCE))
(constraint (= (f #x0000000000000001) #xFFFFFFFFFFFFFFFE))
(check-synth)
