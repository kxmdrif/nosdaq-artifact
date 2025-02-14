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
(constraint (= (f #xBE169945AC7D6317) #x5F0B4CA2D63EB18C))
(constraint (= (f #x2B9D72A432E9153F) #x15CEB95219748AA0))
(constraint (= (f #x6085C6BDFB0F880B) #x3042E35EFD87C406))
(constraint (= (f #x5DE715F44AB915E3) #x2EF38AFA255C8AF2))
(constraint (= (f #x2EC7D4CF051FDC63) #x1763EA67828FEE32))
(constraint (= (f #x06ADB959822844D5) #xF2A48D4CFBAF7656))
(constraint (= (f #x809E8CCF7E1FCCE1) #xFEC2E66103C0663E))
(constraint (= (f #xC5962F6E0E01A02D) #x74D3A123E3FCBFA6))
(constraint (= (f #x1904B1FF358EC79D) #xCDF69C0194E270C6))
(constraint (= (f #xC972F26950ACD5B1) #x6D1A1B2D5EA6549E))
(constraint (= (f #xFFFFFFFFFFFEBF8B) #x7FFFFFFFFFFF5FC6))
(constraint (= (f #xFFFFFFFFFFFEC207) #x7FFFFFFFFFFF6104))
(constraint (= (f #xFFFFFFFFFFFE0B0B) #x7FFFFFFFFFFF0586))
(constraint (= (f #xFFFFFFFFFFFE7A7F) #x7FFFFFFFFFFF3D40))
(constraint (= (f #xFFFFFFFFFFFECEEF) #x7FFFFFFFFFFF6778))
(constraint (= (f #x1FFFFFFFFFFFFFFF) #x1000000000000000))
(constraint (= (f #xE58303F5F495673E) #x72C181FAFA4AB39F))
(constraint (= (f #x54250F036E4AEBBA) #x2A128781B72575DD))
(constraint (= (f #x10B2DF7F12CE6F9E) #x08596FBF896737CF))
(constraint (= (f #xEA4DFF41BBE515E2) #x7526FFA0DDF28AF1))
(constraint (= (f #x2195FD9EA55BF5C6) #x10CAFECF52ADFAE3))
(constraint (= (f #xFFFFFFFFFFFEF5CD) #x7FFFFFFFFFFF7AE7))
(constraint (= (f #xFFFFFFFFFFFE7999) #x7FFFFFFFFFFF3CCD))
(constraint (= (f #xFFFFFFFFFFFEEC29) #x7FFFFFFFFFFF7615))
(constraint (= (f #xFFFFFFFFFFFEE1C1) #x7FFFFFFFFFFF70E1))
(constraint (= (f #xFFFFFFFFFFFE2C31) #x7FFFFFFFFFFF1619))
(constraint (= (f #x008311D4DF5CA5E0) #xFEF9DC564146B43C))
(constraint (= (f #x3E70B0AB473474F8) #x831E9EA97197160C))
(constraint (= (f #x9E568C828D80CF2C) #xC352E6FAE4FE61A4))
(constraint (= (f #xC8892E08C943515C) #x6EEDA3EE6D795D44))
(constraint (= (f #x1837F6C5D9C420F8) #xCF9012744C77BE0C))
(constraint (= (f #xFFFFFFFFFFFEFA22) #x0000000000020BB8))
(constraint (= (f #xFFFFFFFFFFFE5D16) #x00000000000345D0))
(constraint (= (f #xFFFFFFFFFFFE9E42) #x000000000002C378))
(constraint (= (f #xFFFFFFFFFFFE23FE) #x000000000003B800))
(constraint (= (f #xFFFFFFFFFFFED4D2) #x0000000000025658))
(constraint (= (f #xFFFFFFFFFFFEC010) #x0000000000027FDC))
(constraint (= (f #xFFFFFFFFFFFEDC70) #x000000000002471C))
(constraint (= (f #xFFFFFFFFFFFEB9F0) #x0000000000028C1C))
(constraint (= (f #xFFFFFFFFFFFEA634) #x000000000002B394))
(constraint (= (f #xFFFFFFFFFFFEE5E4) #x0000000000023434))
(constraint (= (f #x4F8D5A82CDF0FDCE) #x27C6AD4166F87EE7))
(constraint (= (f #x0E571EC6F5717AEF) #x072B8F637AB8BD78))
(constraint (= (f #x885657B2F7E8D06D) #xEF53509A102E5F26))
(constraint (= (f #x555459E7971BA83F) #x2AAA2CF3CB8DD420))
(constraint (= (f #x8CDD36C2A7415171) #xE645927AB17D5D1E))
(constraint (= (f #xAFFD6832D76079A8) #xA0052F9A513F0CAC))
(constraint (= (f #x198D95EC6CDAF5C0) #xCCE4D427264A147C))
(constraint (= (f #x786053A3272B7032) #x3C3029D19395B819))
(constraint (= (f #x8C7F32F339A12888) #xE7019A198CBDAEEC))
(constraint (= (f #xD6EE9376317B175D) #x5222D9139D09D146))
(constraint (= (f #xFFFFFFFFFFFE41AA) #x0000000000037CA8))
(constraint (= (f #xFFFFFFFFFFFE4149) #x7FFFFFFFFFFF20A5))
(constraint (= (f #xFFFFFFFFFFFE4598) #x00000000000374CC))
(constraint (= (f #xFFFFFFFFFFFE33CF) #x7FFFFFFFFFFF19E8))
(constraint (= (f #x3F193E1916100011) #x81CD83CDD3DFFFDE))
(constraint (= (f #xFFFFFFFFFFFFFFFF) #x0000000000000000))
(check-synth)
