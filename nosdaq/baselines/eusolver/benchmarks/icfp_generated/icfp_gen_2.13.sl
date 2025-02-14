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
(constraint (= (f #x8165DADA5812E524) #x0000000000000000))
(constraint (= (f #x3F8C304728108EBA) #x0000000000000000))
(constraint (= (f #xC809607EB9BC8958) #x0000000000000000))
(constraint (= (f #x4450D6B51F26B074) #x0000000000000000))
(constraint (= (f #xE93597B2543206FA) #x0000000000000000))
(constraint (= (f #x6593A5170BAD4EF0) #x0000000000000002))
(constraint (= (f #x9A32C912E4B55D64) #x0000000000000002))
(constraint (= (f #xB7A36DDED64FE7A4) #x0000000000000002))
(constraint (= (f #x54DAF783EC1733D4) #x0000000000000002))
(constraint (= (f #x5CD4AE1C69C3B2FC) #x0000000000000002))
(constraint (= (f #x0000000000000018) #x0000000000000000))
(constraint (= (f #x000000000000001C) #x0000000000000000))
(constraint (= (f #x0000000000000010) #x0000000000000000))
(constraint (= (f #x000000000000001A) #x0000000000000000))
(constraint (= (f #x3722C74314698969) #x0000000000000002))
(constraint (= (f #xEF98B9BD2DC97B91) #x0000000000000002))
(constraint (= (f #xA0729CC4266746F5) #x0000000000000002))
(constraint (= (f #x4677C2AA57BB6597) #x0000000000000002))
(constraint (= (f #xAF8CCCD47E7B9FF1) #x0000000000000002))
(constraint (= (f #x000000000001F804) #x0000000000000002))
(constraint (= (f #x00000000000189F4) #x0000000000000002))
(constraint (= (f #x00000000000148DA) #x0000000000000002))
(constraint (= (f #x0000000000017B76) #x0000000000000002))
(constraint (= (f #x0000000000013C40) #x0000000000000002))
(constraint (= (f #x6FCBAA2CF242EB65) #x0000000000000000))
(constraint (= (f #x504C31D9447CC42F) #x0000000000000000))
(constraint (= (f #x55D05EA29FFCC3F3) #x0000000000000000))
(constraint (= (f #xE8130E42DC46C3D7) #x0000000000000000))
(constraint (= (f #x22666CC8CA162DD7) #x0000000000000000))
(constraint (= (f #xC9201B01B245C1B3) #x0000000000000000))
(constraint (= (f #x9260814448899039) #x0000000000000000))
(constraint (= (f #x098008B809113225) #x0000000000000000))
(constraint (= (f #xE001605160516039) #x0000000000000000))
(constraint (= (f #x9A20BA2A0C191039) #x0000000000000000))
(constraint (= (f #x0000000000018CD1) #x00000000000319A2))
(constraint (= (f #x00000000000118F5) #x00000000000231EA))
(constraint (= (f #x0000000000013A73) #x00000000000274E6))
(constraint (= (f #x000000000001C67F) #x0000000000038CFE))
(constraint (= (f #x0000000000018A05) #x000000000003140A))
(constraint (= (f #x0000000000000015) #xFFFFFFFFFFFFFFD4))
(constraint (= (f #x0000000000000019) #xFFFFFFFFFFFFFFCC))
(constraint (= (f #x000000000000001D) #xFFFFFFFFFFFFFFC4))
(constraint (= (f #x000000000000001B) #xFFFFFFFFFFFFFFC8))
(constraint (= (f #x0000000000000017) #xFFFFFFFFFFFFFFD0))
(constraint (= (f #xD9124C926C06C1F1) #x0000000000000000))
(constraint (= (f #x06418202AA02C0A9) #x0000000000000000))
(constraint (= (f #xE0E00D809820282B) #x0000000000000000))
(constraint (= (f #xC103A2C064D8326D) #x0000000000000000))
(constraint (= (f #x04C9A206064048B1) #x0000000000000000))
(constraint (= (f #x00000000000120B1) #x0000000000000000))
(constraint (= (f #x0000000000017027) #x0000000000000000))
(constraint (= (f #x000000000001C1B3) #x0000000000000000))
(constraint (= (f #x000000000001003D) #x0000000000000000))
(constraint (= (f #x000000000001C031) #x0000000000000000))
(constraint (= (f #x646E74E7EE1E9DC8) #x0000000000000000))
(constraint (= (f #x7B848BCC14B33CD1) #x0000000000000002))
(constraint (= (f #x5084C9370CF9ED12) #x0000000000000002))
(constraint (= (f #x3D17E1443F9735CA) #x0000000000000002))
(constraint (= (f #x80EDA7B0BEC43283) #x0000000000000000))
(constraint (= (f #x0EA1E92798416BF3) #x0000000000000002))
(constraint (= (f #x58812E977C1BD394) #x0000000000000002))
(constraint (= (f #x2F7D569252D2F115) #x0000000000000000))
(constraint (= (f #x9454F6B2F8CBB89B) #x0000000000000002))
(constraint (= (f #xBEFD4AE4453D1288) #x0000000000000002))
(constraint (= (f #x000000000000001D) #xFFFFFFFFFFFFFFC4))
(constraint (= (f #x05C17403C01D01B3) #x0000000000000000))
(constraint (= (f #x0000000000011EA7) #x0000000000023D4E))
(constraint (= (f #x92E61D6C0844795E) #x0000000000000000))
(constraint (= (f #x0000000000000016) #x0000000000000000))
(constraint (= (f #xBFB13C88DFFE0E7C) #x0000000000000000))
(constraint (= (f #xC39D4508980F6AFE) #x0000000000000002))
(constraint (= (f #x9690783C102D0C1B) #x0000000000000002))
(constraint (= (f #x4A2424FDCB69FC27) #x0000000000000002))
(constraint (= (f #xBD608A363044227B) #x0000000000000000))
(constraint (= (f #x2AA520A49108409E) #x0000000000000000))
(constraint (= (f #xF04A00878042503D) #x0000000000000000))
(constraint (= (f #xA910028514890A16) #x0000000000000002))
(constraint (= (f #x29501252A550AA9F) #x0000000000000000))
(constraint (= (f #x000000000001A2F8) #x0000000000000002))
(constraint (= (f #x5442920AA511E0D5) #x0000000000000002))
(constraint (= (f #x8AEDA5DD2DCAEBFE) #x0000000000000000))
(constraint (= (f #x336E8C31BDF1FE41) #x0000000000000002))
(constraint (= (f #x000000000001F035) #x0000000000000000))
(constraint (= (f #x000000000001311D) #x000000000002623A))
(constraint (= (f #x000000000001D7BB) #x000000000003AF76))
(constraint (= (f #x000000000001CE75) #x0000000000039CEA))
(constraint (= (f #x000000000001C1A1) #x0000000000000000))
(constraint (= (f #x00000000000192A8) #x0000000000000002))
(constraint (= (f #x00000000000120A3) #x0000000000000000))
(constraint (= (f #x000000000001FF9D) #x000000000003FF3A))
(constraint (= (f #xAA2C906C8040C8A9) #x0000000000000000))
(constraint (= (f #x1FB3A0080601F86D) #x0000000000000002))
(constraint (= (f #x2001C01146180A39) #x0000000000000000))
(constraint (= (f #x0000000000000C0B) #x0000000000000000))
(check-synth)
