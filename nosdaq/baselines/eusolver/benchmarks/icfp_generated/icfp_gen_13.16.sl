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
(constraint (= (f #x7992782E2528B4D9) #x71F4A0533885C06B))
(constraint (= (f #x93CF632DA1BA5F0D) #x950C6AE0845E0502))
(constraint (= (f #xD9BE8F177D449A19) #xDBDA9819F56F2C47))
(constraint (= (f #x2C74FF8EB3083D57) #x214C4F89A7C7417D))
(constraint (= (f #x3E53EBB5CC80FAE3) #x32492AF16FB70AB2))
(constraint (= (f #xEE5D2C441E6623BA) #xE1A2D3BBE199DC45))
(constraint (= (f #x8EF2C7DDC44665D6) #x810D38223BB99A29))
(constraint (= (f #x41C0625D4C0CE7B6) #x4E3F9DA2B3F31849))
(constraint (= (f #xE26F4007BAAA5BF4) #xED90BFF84555A40B))
(constraint (= (f #x0F3715024F563DD6) #x00C8EAFDB0A9C229))
(constraint (= (f #xE83AF332380FDF33) #xE946A3FEE470DD3F))
(constraint (= (f #xE1374120CCBB6E55) #xE0DBCACD3F8F274F))
(constraint (= (f #x1373F334D3355B1B) #x1DBB33F861F9F155))
(constraint (= (f #x432DCAFADB254413) #x48E0E9AA8968EFAD))
(constraint (= (f #x816F0A8B3FF95F45) #x868605DC73F9354E))
(constraint (= (f #xD8DF45EA4267F6A0) #x9090450A424404A0))
(constraint (= (f #x91F27886D6A9432C) #x9102408494A94228))
(constraint (= (f #x2A2A6A9E2F71D420) #x2A2A4A9028411420))
(constraint (= (f #x3DB2DB00D3D165D0) #x2122920092114510))
(constraint (= (f #x316CA540428FD1EE) #x2148A54042881108))
(constraint (= (f #xDA7C30F764762A85) #x9240208444442A85))
(constraint (= (f #xC86AA96D4B0E54FF) #x884AA9494A085480))
(constraint (= (f #x20819CBF0F6849BF) #x208110A008484920))
(constraint (= (f #xDBDD7852EC4CEE9F) #x9211405288488890))
(constraint (= (f #xC9F12613079A484B) #x890124120412484A))
(constraint (= (f #xD49E4D5CCE1E385A) #xDB61B2A331E1C7A5))
(constraint (= (f #x1B71C9ACBD0A7702) #x148E365342F588FD))
(constraint (= (f #x0E28D45914F4D222) #x01D72BA6EB0B2DDD))
(constraint (= (f #x65243E575F5E0BC0) #x6ADBC1A8A0A1F43F))
(constraint (= (f #x45EF0302EA16551E) #x4A10FCFD15E9AAE1))
(constraint (= (f #x66D58697DB357A23) #x4495049412254222))
(constraint (= (f #x9DEE99044495B92D) #x9108910444952129))
(constraint (= (f #xB78E6867AE959F97) #xA408484428951014))
(constraint (= (f #x3FB78589B2ADA433) #x2024050922A92422))
(constraint (= (f #xCAB999AC19037ECD) #x8AA1112811024089))
(constraint (= (f #xA95B6DD808CBEB60) #xA9524910088A0A40))
(constraint (= (f #xD6EBF89BF0E19476) #x948A009200811444))
(constraint (= (f #x086437587D9B81D6) #x0844245041120114))
(constraint (= (f #xB228E1B5726DC92A) #xA22881254249092A))
(constraint (= (f #xD779503A56854662) #x9441502254854442))
(check-synth)
