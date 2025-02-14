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
(constraint (= (f #xCE9948310B2A756A) #xCE99551A9FAD861C))
(constraint (= (f #xCEB3C9F8B2049ECA) #xCEB3D6E3EEA429EA))
(constraint (= (f #xA5274724521DD780) #xA5275176C6901CA1))
(constraint (= (f #xD618542F86329BC8) #xD61861910B75942B))
(constraint (= (f #xBD9C9A6FF69A5964) #xBD9CA649C04158CD))
(constraint (= (f #x4096C023E5A29E3A) #x0000000000000000))
(constraint (= (f #x59F35F5AB3B5CE74) #x0000000000000000))
(constraint (= (f #x8E64FE7B8F2BDAB2) #x0000000000000000))
(constraint (= (f #xEAB2572229E4B87A) #x0000000000000000))
(constraint (= (f #x4FB654A1D1E3A858) #x0000000000000000))
(constraint (= (f #x454533861C37DFE5) #x454533861C37DFE5))
(constraint (= (f #x953C18B91F5885ED) #x953C18B91F5885ED))
(constraint (= (f #x30FC0114587FB72D) #x30FC0114587FB72D))
(constraint (= (f #x3B852D7A405CE06D) #x3B852D7A405CE06D))
(constraint (= (f #x793C58C8668252ED) #x793C58C8668252ED))
(constraint (= (f #xD2008F0EC0D85B17) #x0000000000000000))
(constraint (= (f #x72DE7D9355873BDD) #x0000000000000000))
(constraint (= (f #x80EA550BAF92FB31) #x0000000000000000))
(constraint (= (f #x79DE759890CE107B) #x0000000000000000))
(constraint (= (f #xF27D195D908641DB) #x0000000000000000))
(constraint (= (f #x6AEAC7E04407DBC8) #x6AEACE8EF085E008))
(constraint (= (f #xA76C0B166D05450A) #xA76C158D2DB6ABDA))
(constraint (= (f #x5B64ACE2EAB7E15B) #x0000000000000000))
(constraint (= (f #x26D26EC23CD48120) #x26D2712F63C0A4ED))
(constraint (= (f #xA588512B7CAA625D) #x0000000000000000))
(constraint (= (f #xC7AE5AC750BEB769) #xC7AE5AC750BEB769))
(constraint (= (f #x91EEE7A069287313) #x0000000000000000))
(constraint (= (f #x04EC3B5B27751C63) #x04EC3B5B27751C63))
(constraint (= (f #x32C4685C405CBA7C) #x0000000000000000))
(constraint (= (f #x76709ABE6D95CE7C) #x0000000000000000))
(constraint (= (f #x2A722031763A1AA5) #x2A722031763A1AA5))
(constraint (= (f #x76E16897CD22AEB4) #x0000000000000000))
(constraint (= (f #x0BE4729E5204EE70) #x0000000000000000))
(constraint (= (f #x960D894720DE6370) #x0000000000000000))
(constraint (= (f #xD82B77272B9B4E9D) #x0000000000000000))
(constraint (= (f #xD1AE7B4A0CD6B3E3) #xD1AE7B4A0CD6B3E3))
(constraint (= (f #xE5EE9C2472638C01) #xE5EE9C2472638C01))
(constraint (= (f #xAEB7532EDDBDE58E) #xAEB75E1A52F0D369))
(constraint (= (f #x0C39E0846EB46C6D) #x0C39E0846EB46C6D))
(constraint (= (f #xA55E33E2239A28EB) #xA55E33E2239A28EB))
(constraint (= (f #x07E56E3A98AE8A3A) #x0000000000000000))
(constraint (= (f #x2D5486179783B513) #x0000000000000000))
(constraint (= (f #xCD07C167700D0E42) #xCD07CE37EC238542))
(constraint (= (f #x1EA83EADB37ECCC1) #x1EA83EADB37ECCC1))
(constraint (= (f #xDB0388ECED6D24D7) #x0000000000000000))
(constraint (= (f #xC9DB211B664E24BE) #x0000000000000000))
(constraint (= (f #x1BD8E6117E9955B6) #x0000000000000000))
(constraint (= (f #xEACE3C5D629D3DB2) #x0000000000000000))
(constraint (= (f #xE580146121A3929E) #x0000000000000000))
(constraint (= (f #x5246E85BC14E6890) #x0000000000000000))
(constraint (= (f #x385BBD214D98BC9A) #x0000000000000000))
(constraint (= (f #x834A29BE427C2C1B) #x0000000000000000))
(constraint (= (f #xE107B533E0DCE1AD) #xE107B533E0DCE1AD))
(constraint (= (f #xECBD003A6E4E98DE) #x0000000000000000))
(constraint (= (f #x61B0EE5310EB2D5C) #x0000000000000000))
(constraint (= (f #x184691C32D43D1B3) #x0000000000000000))
(constraint (= (f #xE0984EEBB714D7EC) #xE0985CF53C03935D))
(constraint (= (f #x37BE283A2EE32E8D) #x37BE283A2EE32E8D))
(constraint (= (f #x2843E9C830AE0E82) #x2843EC4C6F4A918C))
(constraint (= (f #xA5ACA99E6D0D0128) #xA5ACB3F937A6E7F8))
(constraint (= (f #x8ED24248BE260B57) #x0000000000000000))
(constraint (= (f #x5E9DA375374A7986) #x5E9DA95F1181CCFA))
(constraint (= (f #xEEEB7290E01B2455) #x0000000000000000))
(constraint (= (f #x620B63D516B7B2E0) #x620B69F5CCF5044B))
(constraint (= (f #xE92876439DB59E92) #x0000000000000000))
(constraint (= (f #x7C07DE786923265A) #x0000000000000000))
(constraint (= (f #x167A7BE0360DA99B) #x0000000000000000))
(constraint (= (f #x22DE9D405DD35A78) #x0000000000000000))
(constraint (= (f #x3EE745A340216ECA) #x3EE74991B47BA2CC))
(constraint (= (f #x51EEBC2AB3071ED7) #x0000000000000000))
(constraint (= (f #xC8E001116DE76363) #xC8E001116DE76363))
(constraint (= (f #xB4377C69EE6B9B9B) #x0000000000000000))
(constraint (= (f #x390C41A98A3CCB20) #x390C453A4E5763C3))
(constraint (= (f #x723EEDAB0DE45842) #x723EF4CEFCBF0920))
(constraint (= (f #xD7488213CEEEE445) #xD7488213CEEEE445))
(constraint (= (f #xA27E70866D621350) #x0000000000000000))
(constraint (= (f #x3AE489E3CD3ABC12) #x0000000000000000))
(constraint (= (f #x1E5100911DE71A0C) #x1E5102762DF02BEA))
(constraint (= (f #x4244CD11A432A6E0) #x4244D135F103C123))
(constraint (= (f #x48CEA9D832C9EA14) #x0000000000000000))
(constraint (= (f #x7998E379EAEA9ED7) #x0000000000000000))
(constraint (= (f #xC60A2EE860797DCE) #xC60A3B49036803D5))
(constraint (= (f #x50A54B4DD6582D3C) #x0000000000000000))
(constraint (= (f #x5CB737D72A73589E) #x0000000000000000))
(constraint (= (f #xBDC6E80701D4E356) #x0000000000000000))
(constraint (= (f #x2410560588416BA6) #x241058468DA1C42A))
(constraint (= (f #x8DADEC459EA9E054) #x0000000000000000))
(constraint (= (f #xAEE3CB130B23C4C0) #xAEE3D60147D4F572))
(constraint (= (f #x746A1DCD35215A41) #x746A1DCD35215A41))
(constraint (= (f #x329E30E82EB9E6A9) #x329E30E82EB9E6A9))
(constraint (= (f #x2226A9E694E77173) #x0000000000000000))
(constraint (= (f #x85C02D0AC00D4A07) #x85C02D0AC00D4A07))
(constraint (= (f #xD77AEE73E4158C12) #x0000000000000000))
(constraint (= (f #x56D63303D1CD44D8) #x0000000000000000))
(constraint (= (f #xBC0EDE10D8887597) #x0000000000000000))
(constraint (= (f #xE95ADDA0E39C8E7D) #x0000000000000000))
(constraint (= (f #xA2790E152B9CB85E) #x0000000000000000))
(constraint (= (f #xCCE5A736A2E09AE5) #xCCE5A736A2E09AE5))
(constraint (= (f #x9DA2E5B9EA8E5C5E) #x0000000000000000))
(constraint (= (f #x58C5C5D585554763) #x58C5C5D585554763))
(constraint (= (f #x89EB7B3C81BACB01) #x89EB7B3C81BACB01))
(constraint (= (f #xED99E3764EE39E5A) #x0000000000000000))
(constraint (= (f #xE500EAB5C17EB5B0) #x0000000000000000))
(constraint (= (f #x7C177CEDBE53E320) #x7C1784AF3622BF05))
(constraint (= (f #xC74D186842594E84) #xC74D24DD13DFD2A9))
(constraint (= (f #x00EC2C62BE7B4E7E) #x0000000000000000))
(constraint (= (f #x6034129769C2E9AE) #x6034189AAAEC604A))
(constraint (= (f #xC94158BDE7B2DCCE) #xC9416551FD3EBB49))
(constraint (= (f #x341DC9E433947A21) #x341DC9E433947A21))
(constraint (= (f #xAA46C6717356E586) #xAA46D115DFBDFCBB))
(constraint (= (f #xA15C059BD25C841E) #x0000000000000000))
(constraint (= (f #x86B5CE9E22B69155) #x0000000000000000))
(constraint (= (f #x1EE9E1CCEEDD3B96) #x0000000000000000))
(constraint (= (f #xEA744BD74D1E4829) #xEA744BD74D1E4829))
(constraint (= (f #xB8EEBEDA96E8417C) #x0000000000000000))
(constraint (= (f #xC19AAC9C5EE5C0DE) #x0000000000000000))
(constraint (= (f #x0DE9CDCCA0CAED60) #x0DE9CEAB3DA7B76C))
(constraint (= (f #xD54290BC1219C4D2) #x0000000000000000))
(constraint (= (f #xE5A8EEC8E9E8D6BE) #x0000000000000000))
(constraint (= (f #x27D7E26CEBD19A6C) #x27D7E4EA69F86929))
(check-synth)
