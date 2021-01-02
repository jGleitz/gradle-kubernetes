package de.joshuagleitze.gradle.kubectl

import de.joshuagleitze.gradle.kubectl.data.KubectlDistribution
import de.joshuagleitze.gradle.kubectl.data.KubectlRelease
import de.joshuagleitze.gradle.kubectl.data.Linux
import de.joshuagleitze.gradle.kubectl.data.MacOs
import de.joshuagleitze.gradle.kubectl.data.Version
import de.joshuagleitze.gradle.kubectl.data.Windows

object KubectlVersion {
  val V1_19_3: KubectlRelease = KubectlRelease(
        Version(1, 19, 3),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.3/bin/linux/amd64/kubectl",
          sha512Hash = "4bd43302a46eb9d15a2ee1db2acc874e36dc862982022528350baad2e283c8ec64fd1935d7ed62dc7c534c36230132121357851cd6ffd9b768f5807d344ccd32"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.3/bin/darwin/amd64/kubectl",
          sha512Hash = "e90020b6ed36b1ba2c55b54bc15afe2a8e07b0340d440dec72eb85f9a8b78be8c16355a3db4fec43446bd3b7f9e4aa4b53cb675c537f3261b1d8416d6c5b4d1b"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.3/bin/windows/amd64/kubectl.exe",
          sha512Hash = "409b131ea6f22108a56069a75c7b427452df351a2fa9a8198eaf6c91d1177f6719d1f0b45702f3cd4dc78ae415c3016faef4d7d4ec55f2e29a2cd656df858dac"
        )
      )

  val V1_19_2: KubectlRelease = KubectlRelease(
        Version(1, 19, 2),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.2/bin/linux/amd64/kubectl",
          sha512Hash = "3d5e4d5552de205447814ab98f3e230459586bc3682f65cd9c392fde9cb227718b73959d59c960d8cdcc58810d031e41ddd4f2c48f8f6f25d0fe4ff7951e8f1e"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.2/bin/darwin/amd64/kubectl",
          sha512Hash = "f36c90cbca016a6815ade0973b48334c0d2514eafefa65102da34275c88207689dbd87aa707659c4c4d709ad204cb48adf4fc19843ff4ded62cd3c0bfd0a25b3"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.2/bin/windows/amd64/kubectl.exe",
          sha512Hash = "1491c5bcfd7eb47255b1797bbcb68fb283e31830f1a36f585bcbc49f591e535775d334596ded5bdd23adb60798617ab63d184f3cd1b3fb5746810eb5aef01b87"
        )
      )

  val V1_19_1: KubectlRelease = KubectlRelease(
        Version(1, 19, 1),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.1/bin/linux/amd64/kubectl",
          sha512Hash = "65f9fdb0edcd60edc4f909fcd10a3f2c0fe023c6bf8c6f0762122a8f9be0a3b8c67b5ffa2905ada1bf190421fe6a6cd093f374fcfd9e9df49755b447a983aa53"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.1/bin/darwin/amd64/kubectl",
          sha512Hash = "f301982329b597e424a213df8c72e4d62569e5e9536322e39db2011ee8e22382261480667ad43bf969218b6baad0e021f4c2ce95142f02927b6c615f7729b444"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.1/bin/windows/amd64/kubectl.exe",
          sha512Hash = "f25bf83fa49a79288501bdf5ddc221adfd678242bfd960d5e9a3e62ec1989ffd977f2dc318973ff601ae416062ca6025a1d7dfdba6108af14b3d8e542744ea70"
        )
      )

  val V1_19_0: KubectlRelease = KubectlRelease(
        Version(1, 19, 0),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.0/bin/linux/amd64/kubectl",
          sha512Hash = "e27b8d65b49296be4366b828ef179416e50f22029d6a927e5b111ff001f553ce4603426121c1502575ee19613ff14203989b5cd42998684b66a053ae32e570ee"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.0/bin/darwin/amd64/kubectl",
          sha512Hash = "f069b1d2b3a1ae4a6fde82682c35948f5e8e9c4326bda7ee78156f385fc68c4bbc1b9e1773de9bb86f01454e8c7a56a52c566373f25edbac044b8189a1ef3623"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.19.0/bin/windows/amd64/kubectl.exe",
          sha512Hash = "f2ec7f975087677972198bd5792f3ba5a6b9ed90bb91dbf780b0f23b33c746c9b2c039a20db234367f356ff15defc000e5dbba6133a25d7c28d433832a709f26"
        )
      )

  val V1_18_10: KubectlRelease = KubectlRelease(
        Version(1, 18, 10),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.10/bin/linux/amd64/kubectl",
          sha512Hash = "0b129b1f373a56d880a9e80ada66146303a37536e20ae2f96baa1ed3b6ec1bed6be6648e27d5fa9c0fb62378483ea37705690f8c4b94ede3d8ad0e91b4def3cf"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.10/bin/darwin/amd64/kubectl",
          sha512Hash = "ece1cd51f7aa3d39ddb5f8266f973f91fe84b3ce4472df16466d47a730a15bdad5e66bf8d24669c2db7335e5c6c8005bd3e9805e876de9669c729214a73fdb16"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.10/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b0ff0d10801578079a14a6c658b3e2971a3ab8523a9aaedefe015d0505c6265aa9f593087ff5aa810937f4cdb8ac70fcca4a48e1c321154b8c5df54834184857"
        )
      )

  val V1_18_9: KubectlRelease = KubectlRelease(
        Version(1, 18, 9),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.9/bin/linux/amd64/kubectl",
          sha512Hash = "09f1aa42f73d7475a2b8dc2069f31c0846d090d3b8fbafd1c505d84d5a085e9c9193a832fa40f8cd0a0677e8c06441c00b98fcfc33ba8b686950bf6cad9138ac"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.9/bin/darwin/amd64/kubectl",
          sha512Hash = "dfc4b90374f3e8b8297e922f063a6894f94a9136b5bf39d82f5c28e36ed56aac42caccba5e0c0725921a02e9e39c5cec3c38e0c001464abc419c6c0fb86a8c69"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.9/bin/windows/amd64/kubectl.exe",
          sha512Hash = "23e83fc1193e6c6ac4a61273b0bfbab1b5304d1a2520d47ca4bba77afe4484b7d655bdc04ec53dbc8c0a87b8a854fdb4dbc997fc09a880ee3ce08abfb852c974"
        )
      )

  val V1_18_8: KubectlRelease = KubectlRelease(
        Version(1, 18, 8),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.8/bin/linux/amd64/kubectl",
          sha512Hash = "c19125e4467098ec61143af2583bdd423a9fd5ce25d7babd1f1153f262f3d97a274ec81005da593e1fcf5a9c6a65668d54c3f0ab0fa8a29a8f13b4b6c0fb3b88"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.8/bin/darwin/amd64/kubectl",
          sha512Hash = "d1a6c8cc566546cd62be6dc93c7d0088edeb17537eced25a4d2745140cae973b513b55b4d4c88fefba8cc9ab8ae92c18d5bfdcdd94a04cc45cd00fab408ccfeb"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.8/bin/windows/amd64/kubectl.exe",
          sha512Hash = "8102356612c1d9c4299e4ddcedf06255c87c7941a2d148e869ce70930b74fc7d64243e009021cad42391c528af005b404879993b8eeccae0527a7a71d66fa690"
        )
      )

  val V1_18_6: KubectlRelease = KubectlRelease(
        Version(1, 18, 6),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.6/bin/linux/amd64/kubectl",
          sha512Hash = "2d523472d0caef364ca6fbdabb696a67d93aa108b13597e396d35027681345b52a36f88e7b7e1a63620b5ca0dcbc954c7320dc23a84c7d0f2019bf2c76e6065d"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.6/bin/darwin/amd64/kubectl",
          sha512Hash = "27b3b981222e50633b83cc185757fc050d84c7a436bb196775c5b990c3c8ac904d9067172113ca946d6f5d50607a49e7ff73f0e8c2474f9bbc22458bae9b0630"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.6/bin/windows/amd64/kubectl.exe",
          sha512Hash = "745d8fd1fd4b088f0aefe1e6c2f9f36974c0b16a118d3fd1c66e5fff2af22af6753e65cb9d6f03285f330ea2abe036b31c9e3cd92044e241b50e83bf9da17fe2"
        )
      )

  val V1_18_5: KubectlRelease = KubectlRelease(
        Version(1, 18, 5),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.5/bin/linux/amd64/kubectl",
          sha512Hash = "5a99ce73a14da6ab561016b83906c398f082dd7571771b6b16430445f6668587a2ad2f5ca9e5a330bcc3fad5f4a7f21109042defcd674acae8cd7170b49ed947"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.5/bin/darwin/amd64/kubectl",
          sha512Hash = "912c09e86a670dcc3faf4dbdf73d85ab37884ed728a436f66cd7e7a073488538abafbed27daaddbe18ffc080b0bb93a31a6cc5cfec292d5a46b4afa8be5f2fa1"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.5/bin/windows/amd64/kubectl.exe",
          sha512Hash = "51515bb36e5cfd5b90fc1490318d166a1080b59ecebb9f83217786eeb51a28a44a1dc6752d8fd3c473e0048cb376be3831ae2cf772c08ee864a56c1a4625a64b"
        )
      )

  val V1_18_4: KubectlRelease = KubectlRelease(
        Version(1, 18, 4),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.4/bin/linux/amd64/kubectl",
          sha512Hash = "84ba8b1bad8ac4f926e2a91be5c752d3f40afde13be152a24184cee4d1b455e7ae304f8c85c8950d01d664151880a58d7e9fd8414aa778b6e479dc663060ed07"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.4/bin/darwin/amd64/kubectl",
          sha512Hash = "bcb0db8bdd932f494c62cd6beb5aefd788d0550ca98e943f8ece17ca84800abbdbc4f799add0f59902c9df2d0f8c9468cc91a585735b3c59ce697d9fff04c085"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.4/bin/windows/amd64/kubectl.exe",
          sha512Hash = "813044fa884b7dc5da0c08e55e7bd982afbce0bad1d0242af673938d21f834144dce7e7e9b7a90309d5c30d2561e3fdbcea1ca5bba481e15233af6b783856d26"
        )
      )

  val V1_18_3: KubectlRelease = KubectlRelease(
        Version(1, 18, 3),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.3/bin/linux/amd64/kubectl",
          sha512Hash = "53c9606d2b25e02520c635b2d1b3b45992a8aa118de672be6922fe793e16ecdf5fe680ab78b5ed2dfda94b869e889d8ba553698903f8b94cd41cc55fc42641e5"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.3/bin/darwin/amd64/kubectl",
          sha512Hash = "4a27a4c81b4898797189e9690f5a08946ad1597dda5f1ac0f00a5083234b474cbd6396a3e8433cfad3d8ce6f7a5a0c09242c70c922ab052f0ae8f6416f8e5161"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.3/bin/windows/amd64/kubectl.exe",
          sha512Hash = "3235e6c4c83214fe561457e35b9f3c2f26cdd83454bfb07b2eeac8c034fa822f7898e8ccafa1966e3d54271426fad56427306d9be01ffd69ad7441d338a9d6b2"
        )
      )

  val V1_18_2: KubectlRelease = KubectlRelease(
        Version(1, 18, 2),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.2/bin/linux/amd64/kubectl",
          sha512Hash = "0cba4d798ea1bfd75e641afde1f02ab16ed38546c0ffa414cd3afa2a4b52607cc6a8781a4f4f25cb2fe608d08330ff6ed065a4891a6bf914b4a96ec3995295bc"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.2/bin/darwin/amd64/kubectl",
          sha512Hash = "5157d2a48953740aafb0d26cb9a07ed1a68d329e7befde1693d0346c8c7b0adad2f8df4d2c66979c77f0399e8413dedd34c0d38016804bc65092e8c5a36457b6"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.2/bin/windows/amd64/kubectl.exe",
          sha512Hash = "fe6f7c02c358c082ef25e9dad276a7fd680cf4702c4d539e9b28bb0215f4c033a554b7b0b6868e3506f9686744a394e8c9af7631d4b954ee3cabfa2366f70f24"
        )
      )

  val V1_18_1: KubectlRelease = KubectlRelease(
        Version(1, 18, 1),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.1/bin/linux/amd64/kubectl",
          sha512Hash = "05ebed6e5f35bb961e172de2c42244641ee5f02e42aa972809d3eb0250f91ca29b852bdc083ccc47abccecd24bb57bf6891b889f2eb79976079144699f2584ee"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.1/bin/darwin/amd64/kubectl",
          sha512Hash = "781ce3aa3b3ffadd30788334bf5729a2d1a5eed6ef6cbaf1d242973f00a59cbe75a9a47f864d253e3c18702cf006f0c94b038a4d7b089881ebe387e6507ff2c6"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.1/bin/windows/amd64/kubectl.exe",
          sha512Hash = "c1288672fa033509336f6e73e5b4f85e342c36450e874c14787fae4e12f77e0b2d7fa20082ba39b07fa6f7c63ac9fefd1c31039ffe13fe2139e80fa3eae3e931"
        )
      )

  val V1_18_0: KubectlRelease = KubectlRelease(
        Version(1, 18, 0),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/linux/amd64/kubectl",
          sha512Hash = "66d7178fa84f62684d3ed46815fcd9a938d589485ae04d80e242cd30da6906d3ffc17a708f05ff3a303e1d6590a11b6905290f275c9488f9195c3037214bb832"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/darwin/amd64/kubectl",
          sha512Hash = "803a3507d663b82d6ce3334d3cb711c53538a7452a46c28fc15d1f51fc0cc221eb37142073e95b6527ef3ed9d0d776b3df6463c557762486b700c1fbccd3ce67"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/windows/amd64/kubectl.exe",
          sha512Hash = "84998e12c625f0c413467934933cfdf060cffb3a762e74bb6535d92f215a898f48ce232702383fdabe6d74d9cd8cd870c9873c297bc587fa2c1d0d8cc0db0f04"
        )
      )

  val V1_17_13: KubectlRelease = KubectlRelease(
        Version(1, 17, 13),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.13/bin/linux/amd64/kubectl",
          sha512Hash = "d3531e5137cbda776ae00924d2351b0d593abcb8e23c39d48307c5d707132a034460fda47ec581641e2e7fb218676c91a3afb3b9af17692ca82fa3dc25313065"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.13/bin/darwin/amd64/kubectl",
          sha512Hash = "edab64eec46fc33f549dd34f2077de3a7a73bf56b55fd3988420f22dfce3080fec11b36945c7dc2caf7d763db17f767b5fadd61cfb91d8ab657f17f8a71cb3d2"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.13/bin/windows/amd64/kubectl.exe",
          sha512Hash = "87549d7663b8e4a7ba8a7c3000324fcfff333c5ca8db3827404e07a68bd51b57dce30d7b82f1e2444c304c46ebfcde9b59ad1d0133308911cfedcbec08cc20ec"
        )
      )

  val V1_17_12: KubectlRelease = KubectlRelease(
        Version(1, 17, 12),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.12/bin/linux/amd64/kubectl",
          sha512Hash = "de94122b3b392625acac0240704abb3ad59f563ebb92fdc9861378c59ccb696e8084a8eae715494d2f0489f31db8b3c257d3de8256aa1d9c4530323055ab91e1"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.12/bin/darwin/amd64/kubectl",
          sha512Hash = "6899517b0089cbce4ae6a8aeab647c3b5d38efd858f5018b36702295dbba388b4c2b606275e198ed7b8ed90ea70a24ff6ea4b6cd95a0d45d99e42ef09f108740"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.12/bin/windows/amd64/kubectl.exe",
          sha512Hash = "a5680c6a026523f3b41d6e0da37accde96987665270593e35e74e7e1150080f37e348059ecc867689fdc832a53916852a5f454d8b9f5f28e2cadc46b6ed0ea83"
        )
      )

  val V1_17_11: KubectlRelease = KubectlRelease(
        Version(1, 17, 11),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.11/bin/linux/amd64/kubectl",
          sha512Hash = "2e37d8842a41d5e46ae9ec4b40b272c8a8a5458e99dc233794b35514f90fb548d3903009a11a4373ad804f551f002d95eeac86cebbb82ca27ed0b49166e267e3"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.11/bin/darwin/amd64/kubectl",
          sha512Hash = "f6188a3cb88c238c03423b05c78414abee463507e8bab9ab7038f885615be4fd896a2d8824340349422e7966d605e97991316c08237e623045728fcba76d6a1f"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.11/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b700a0b2e1776702553ba909576c39d57794411c77406708d32977526695a7e1716046c5243d7d76d6a7fc63b341f59e218dcce8396ed07648d2a26c6d03f227"
        )
      )

  val V1_17_9: KubectlRelease = KubectlRelease(
        Version(1, 17, 9),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.9/bin/linux/amd64/kubectl",
          sha512Hash = "405779ca568b5eb7ebc0dd18c628ef59beaefcd176c4ef9e03569ddb1d163a09f551d5c81bcf4cf0a5f938769c9f61796bc846b0d6e02b92a14df00f73f61e9d"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.9/bin/darwin/amd64/kubectl",
          sha512Hash = "f704f8c333ca7c1d2d99e703ac742a3eebbf374ec93774ca09f82ce28d94d736fc28366af4b2d6bbe01ab0db04af91fe997a45b303417af11718702d962ffc69"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.9/bin/windows/amd64/kubectl.exe",
          sha512Hash = "5a1cf4278f21b43dde202dd1a3d09798986180a6ccd9ca048adc089d6c68c2f6cc1104e3d26b3e506c642707ad09515e3ba04180ac6d5d7b8e8c35356d5b467b"
        )
      )

  val V1_17_8: KubectlRelease = KubectlRelease(
        Version(1, 17, 8),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.8/bin/linux/amd64/kubectl",
          sha512Hash = "ef8c7d0d06853a70ff53bedc8f4f4cea1caf80d494e0167c879f66899ccb00768406304b9c3627ccabc4836a91bf9859db831669cd26ee45abd9584edb68102c"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.8/bin/darwin/amd64/kubectl",
          sha512Hash = "a5805d93bf4a99e0f3cb7eee95a09c0ec70c7a37df733892c2034c32b0ba45621004537c509eb0a817388d87d8cca74864cca3c76dbeb1c7e8486127c73c5862"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.8/bin/windows/amd64/kubectl.exe",
          sha512Hash = "cf2bfe6135a7778d23ae249bbe91ebcecdb62dc4326c4bb3668ae34610f8701ed10fd1fcae0ea0425f53f5d2bcba5dff3dfa1768ff4275afe3a550d1a2f783d6"
        )
      )

  val V1_17_7: KubectlRelease = KubectlRelease(
        Version(1, 17, 7),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.7/bin/linux/amd64/kubectl",
          sha512Hash = "375e67993a1dda5249afdc79d0de53ec7167bec3179acafa0342d756b985ddaebac5bb32d4ddb3b2cc8cb1cf283124e0b3d1ac4f1ff4a2e6a61e8deb7b7f0324"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.7/bin/darwin/amd64/kubectl",
          sha512Hash = "6a74fb410eb1eab14a0f50bf8574ede8740ae48ecf8ae49c4f4c979e3db8c9c5ce87e7d6296a0cbac93c22fd1d1c5b59e4c271bb0b4b1ad72f087cbc2b441b96"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.7/bin/windows/amd64/kubectl.exe",
          sha512Hash = "ca1f21051d6d7fc897a659a49231e02923f3c8124b67ca25600d7d1283ef4bf4855ed2b76c8ec2b2ceb986bd48dbf6d60636246474268348f1bdad5b7965ceb6"
        )
      )

  val V1_17_6: KubectlRelease = KubectlRelease(
        Version(1, 17, 6),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.6/bin/linux/amd64/kubectl",
          sha512Hash = "066bd719a15ed9aafbd67687872566c362fc970733109d4d16174d6b01a9f7adf2e9c9671eec200dcf4207afa3cd3607a9f381195eb9ce9fae04219517501bd8"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.6/bin/darwin/amd64/kubectl",
          sha512Hash = "c3dbe019705e2edbbc11c9931507f3e6b8b206f44e72999bad4762049fef42b76784f45e1cd2701f18866d57a65f0c43b3433371541e1dcc9a4740db4df00e64"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.6/bin/windows/amd64/kubectl.exe",
          sha512Hash = "c81fef5acf7995f069ed556195d167c9d4f567395ec1f3576981845e389707f8456b7230beca8dfb4264090b6f41ad04f5cb9bd09374e79317a03698817cca1d"
        )
      )

  val V1_17_5: KubectlRelease = KubectlRelease(
        Version(1, 17, 5),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.5/bin/linux/amd64/kubectl",
          sha512Hash = "3d8a14d745222cc7c7619798acdad88d35dc2da7e0a2086b2bfe5ef699725dade3fbea2a438f84961bd89703bb6e139cd2cf5398ad03297f44789f69c560673b"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.5/bin/darwin/amd64/kubectl",
          sha512Hash = "818e6ccc7711811f5a76f8176efcabfbefd0c818b81bcf912f904cc75d86fc0f334c6d1ad630f7a4ba20cbc66cfa94846b7910f5fd9f729165372d773c2e414b"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.5/bin/windows/amd64/kubectl.exe",
          sha512Hash = "3f0c3c1d0cd609d968d349a02bad5d6bb77d4191cfec5ff6e9b9b4504489652ae3858e4654156093856facf84a0434592fd54f9cb519f00e9a14ae1785090f2e"
        )
      )

  val V1_17_4: KubectlRelease = KubectlRelease(
        Version(1, 17, 4),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.4/bin/linux/amd64/kubectl",
          sha512Hash = "8149205fa0cfad5a2fd625dae2956c6372640a55c69d6c25c1725695b238b9978eea4485482bc051cc6f282782abfd036104cdaea4cb9deb421777ddead97382"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.4/bin/darwin/amd64/kubectl",
          sha512Hash = "1f2b72cdbb72d96928317a154c7c86a8bee4845e6a29b70f550fad9f6a439d2f9180c6cb07f79e8f81f2f8c97882b580cf55312815bc53ceb11470292e2d0729"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.4/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b523f58964bf55dddb2cb38184070c7bde9599952ab90ff4c611aba78b90ec288e06d216bc5626ce7fca8a4634865df1c505093ca76e38b8c38c2b6d98402282"
        )
      )

  val V1_17_3: KubectlRelease = KubectlRelease(
        Version(1, 17, 3),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.3/bin/linux/amd64/kubectl",
          sha512Hash = "84b0fb401a01f6ff11e35bf6d7778736d8b09f2941ec1188f12de5f552507d68789a8d486db6718ff1a66c345c2f622e066c681478ece9a9c0a3eda7c32890b6"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.3/bin/darwin/amd64/kubectl",
          sha512Hash = "05bcebc90cfe5193008e1a18f7fadf84118e9793b97ea8174dd2acbe067ec9809feeae3efd37dc9f5440ecfc8aaad670e230df2c569f1d7fee551a5fc20319fe"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.3/bin/windows/amd64/kubectl.exe",
          sha512Hash = "c9b40fa0527096b2f3c06807b70d8f8b865b22bb89f1bd9cb6404fadc57561c395fe48a15e0b809d2aed40a97faac671c3f446f6be143d24afe53cceb697eebd"
        )
      )

  val V1_17_2: KubectlRelease = KubectlRelease(
        Version(1, 17, 2),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.2/bin/linux/amd64/kubectl",
          sha512Hash = "0e071381d96485ac4cc14fcaba19913953b11749a46d3657c6d9b8f839a0e91dfdc999eed5a9de6704f3545b4ac8d8e545c815e63a19d441a17442b7926a1931"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.2/bin/darwin/amd64/kubectl",
          sha512Hash = "e1a700da6a7fb33021b410cb54326ca3ac937b7288910a332da7c5d538a7be37c051a1e4d540d159063869b5c7676666d923fe2669703bf2288dd81975b120cb"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.2/bin/windows/amd64/kubectl.exe",
          sha512Hash = "4e7c56c2b807a0572ab9d491d2c6d6bd94bbe154006d8bdd84003a66596b5cefa41df78fa1237567896c6b6d9b03eb11c117555c7aa93e2b1274724b8f0fe843"
        )
      )

  val V1_17_1: KubectlRelease = KubectlRelease(
        Version(1, 17, 1),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.1/bin/linux/amd64/kubectl",
          sha512Hash = "20140daea6ce72b6a95be9b8f1716109d284a1f74894c9394687963ae951cadbfbe0d2f370ad1244b4366ed8898ff03c854d15ade4bdf97ea35c341992c64ca9"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.1/bin/darwin/amd64/kubectl",
          sha512Hash = "b8915a4a8fe2c8f46172ed7f3e31d19a6566c02264041ead6112316c5bcf513dd6da458e09cbcff22d4ac418f8e21118000a4da5b5dff2d2ca2a1dc90dd3eb9e"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.1/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b33a133b28683f0c31444ea8d2119106447c5157ea31abc23f83c74d4fe4347c9bb73761d893a835f72db22d4f4627c4976f8d0af045a2bac719026d8277ac7c"
        )
      )

  val V1_17_0: KubectlRelease = KubectlRelease(
        Version(1, 17, 0),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.0/bin/linux/amd64/kubectl",
          sha512Hash = "74e20113f3e210d3836b0f6a6907ddd573c97fa1ddd14a3156ba3b8f2fc24d12770066761e9ca30e0cfd7df8902b7e7c03e7da137474949b3e0b7e8a28ca3d57"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.0/bin/darwin/amd64/kubectl",
          sha512Hash = "88f9252b15204b5d94bf95e532891b956a431db9758f1aa02a374d6a0b346f7bf31815a3bcb7b18562d96d8fe95cc4e4517dd9a1f0c5cff8ba081805bbe646ee"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.17.0/bin/windows/amd64/kubectl.exe",
          sha512Hash = "c2803d70793fef63985a4edaacf6ad6f92731bcea92f6fb6bea5f6f8e6897c757cfa2b6ad2dd2657ea93622572af60d0afeb999222cac648d0e24ce01fea5702"
        )
      )

  val V1_16_15: KubectlRelease = KubectlRelease(
        Version(1, 16, 15),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.15/bin/linux/amd64/kubectl",
          sha512Hash = "7ac1a23fe587af4f3773144fa1374823b2f591fa5e4d06ba06c2df683175b615dae5c9017ac1d85e08a5063794893b48063fc91deb76ae92a902e61d2206c22d"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.15/bin/darwin/amd64/kubectl",
          sha512Hash = "810348032a72e0d0896a315f886499e155b83435923f2e9114135154d2cf36fb93715c1babfc541bc0b36236642fd47d51bd7abfc0dffca8dc5f9626208cf374"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.15/bin/windows/amd64/kubectl.exe",
          sha512Hash = "23da64155e954ff3eb8756834124a1f9cb9b8375cbae29349776def70669534bc20155e740994cda0d3f3be41a66546bb2eb52375cb551363648648e69cd0daa"
        )
      )

  val V1_16_14: KubectlRelease = KubectlRelease(
        Version(1, 16, 14),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.14/bin/linux/amd64/kubectl",
          sha512Hash = "47cb8cd8ec8c3dc2db2bf7921d79bb72e28a30c7f650e1a31f9948f56fb912c1c7e564a55166fc380caa912251d76f11ec93a21eb3e843a66691af56a7ab899b"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.14/bin/darwin/amd64/kubectl",
          sha512Hash = "dd9c49036163fdc72231df44587ef438fff6df53554ec5fefbefd453fac75b0c368465e424e7ff9deecc258c1ee8a1f193bf7b9cc608eab8e8cc25fe301fff75"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.14/bin/windows/amd64/kubectl.exe",
          sha512Hash = "9ac5255828a364ea405acfafe91043f1ff2a4044bf7760620f98487b13cd75d0a5b51d24f7034cb84d3ee9e3de5a46f57424eedcaf9e6e79f51ff04c5ccdc940"
        )
      )

  val V1_16_13: KubectlRelease = KubectlRelease(
        Version(1, 16, 13),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.13/bin/linux/amd64/kubectl",
          sha512Hash = "3aa9aab2538fe0d9ec21c8c40f805163fc65601ec70f2e88fc7739dbfda8d0b1c7659a1e60efc549b1da6bddc02cb2051dfc7ef95976784862ccbd7871d111ee"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.13/bin/darwin/amd64/kubectl",
          sha512Hash = "6198fbc2b9b3866d1a84c05c1d43d9fa685b907dd33478c37285e2774899b740359686e5e2cdf7559c99f38498714fa4d8bf2ff7f70975535d9af57162d9ef2f"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.13/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b6e952397f704e47a53be14d92a75ab7d708ccfcdce31a8981cdaabfd2cc557eea5eb0dddd3fd31b66b88faadabea42fcc80cb81b7d641080275338e6b91c973"
        )
      )

  val V1_16_12: KubectlRelease = KubectlRelease(
        Version(1, 16, 12),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.12/bin/linux/amd64/kubectl",
          sha512Hash = "4109f1171da6df741215ba83908692b1bbb6234a53b32775eb81d417b7087f0f7ea0a503b56518aab84c09def84d41c93d5f94486cad12860a2fdf7d0585b4f2"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.12/bin/darwin/amd64/kubectl",
          sha512Hash = "4dc57735348c8e64673042e487719f7f6bb20da23a23293db31d9237655c782df245a320a9ae21b0aecb4ca1a98806b5461c4ece25c1b01c28e1e48c5e962d85"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.12/bin/windows/amd64/kubectl.exe",
          sha512Hash = "e8ad11821248b9d36b1d32415dc14e63da9c41666c7ed01087c269514b5371b789f1ca2ec69458a057bc1e9e3335024f01fd4c5da08cc797c26b505bd0ef6862"
        )
      )

  val V1_16_11: KubectlRelease = KubectlRelease(
        Version(1, 16, 11),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.11/bin/linux/amd64/kubectl",
          sha512Hash = "354621705550a3313eb765485bb849a1fdce9136244d16605797209fb7f743692dcc1cebbd9d2d46e5ecf160257e4c25fd73ff78422963af852c9b41e095e7f4"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.11/bin/darwin/amd64/kubectl",
          sha512Hash = "621cbe31a8b3bc1ec2bce61e3619200f23579032b30a5b7f4b533a638c842a63024b6825abcb7a70b5e87cbbfae26f7cbec1f335052b7b77a370a87b02a4162c"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.11/bin/windows/amd64/kubectl.exe",
          sha512Hash = "cfaab5b3e9844f1d4edb170b20c6a6f95475b871931816df29e150bf9f1d14cb61acd184f1096ee03e8ff821aef0e7f5ae655ebc443d5dd5a16c11315130e0bc"
        )
      )

  val V1_16_10: KubectlRelease = KubectlRelease(
        Version(1, 16, 10),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.10/bin/linux/amd64/kubectl",
          sha512Hash = "5d872e051169ea133e749ef926c05370d870eb6e1a6f4a5d655402f5d6a02687c9ba557f644ed35c530a7978e06e359d93e2349674c7ddabb07eb33d56394158"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.10/bin/darwin/amd64/kubectl",
          sha512Hash = "d0b4ce56b9bd0991791f3ed4474765a76b8da14e0d5091cc285b0a96d991155c49ebbc1339223a658aaf270661bfc9956cb539f09867939b927ce5ed866b1d25"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.10/bin/windows/amd64/kubectl.exe",
          sha512Hash = "01b96910690e27ee7fbee7c90047e4650f2a2a8375eba0d0fd08fd40a108d1edb03b029ccca2851835e9d4b0f3f1a0fcd7dcaaa84b5d77466d5a4725bce4ff73"
        )
      )

  val V1_16_9: KubectlRelease = KubectlRelease(
        Version(1, 16, 9),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.9/bin/linux/amd64/kubectl",
          sha512Hash = "48057ad49fb3b4338e0b98cba4501b24776d80488647d0f3c0926e14286578e94514ca09c7b399d086fee20193da9af4bd34bd9e4e5d48ee14fc27a0ea1d6ccf"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.9/bin/darwin/amd64/kubectl",
          sha512Hash = "9188e17b25e0c415a0f76b86230025994ad13e55b08821008498b263cc45fb220364bdc2f931c24afe57b53efb9ba55da6b53a135b201d0819c51d7f4255c478"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.9/bin/windows/amd64/kubectl.exe",
          sha512Hash = "d75ae79996421354ba8fe1161c27b94f30e6532c4337a4741df17af002ec51ad0909f184fa0424a4687dc81308dd33a01e61e03b863bb447353b36778e09c5ab"
        )
      )

  val V1_16_8: KubectlRelease = KubectlRelease(
        Version(1, 16, 8),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.8/bin/linux/amd64/kubectl",
          sha512Hash = "8608a05a4a88cbea3eb306bcfcb4695f53ce6d6255f74f30891fdcbf37d1faacfe5f2ed6011d8a10afe400471a1d01c19c02ffcfacb288182fa0c7701ebb9735"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.8/bin/darwin/amd64/kubectl",
          sha512Hash = "a0080aea3e1c6931a1ab0213a1dc9981ff5c634c512c6872d670a0ee1cc7e1cda33275896dedd33049b8ddfda29d132fe623aebe0cb5721afc206db1be771418"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.8/bin/windows/amd64/kubectl.exe",
          sha512Hash = "f66bc47f87cc4bd105fe45a649c4abcdef69a314cdb6c548892f4faf906ed8c3cc684ba6645e60ebb71a1d8b7d7e0545cdce29af99c4248f63d5c8fa4535e3d4"
        )
      )

  val V1_16_7: KubectlRelease = KubectlRelease(
        Version(1, 16, 7),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.7/bin/linux/amd64/kubectl",
          sha512Hash = "6091b9f51a52374bc1662916f84342e233f3866038ae0997ccfd0dbd0bb0f99eec12df9d102b538fb9740704e599cf7a3f11bae1b1104b55ed7dc66035c6f89b"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.7/bin/darwin/amd64/kubectl",
          sha512Hash = "659548c13286792037295b6ca13ba4ff6a2648e37d9833791e60890fa36fa814701a4b621c0820083b3a98c408a6edf583a588234937797837b3aef21f4545f9"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.7/bin/windows/amd64/kubectl.exe",
          sha512Hash = "44c826418f893100885b39bff670694499f0527f470807997e7569d5f2b6166e420423d4c54f841f10ef56910df0c84056cac7319b9cd4e558c485ddb37747f4"
        )
      )

  val V1_16_6: KubectlRelease = KubectlRelease(
        Version(1, 16, 6),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.6/bin/linux/amd64/kubectl",
          sha512Hash = "ba5a430d4b021183463a0600f006993fc451f70cf53f5b08df0504fba169042e10c6449d8e211f0b8eb10492ef40e734217863fb1bbd596c00770291f3730c95"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.6/bin/darwin/amd64/kubectl",
          sha512Hash = "51c78c50af7a1fd36a396a42d18d4af1dc38c4b84730b70313742ab7714d08bda92937fd2f4bd4851c5e7a1b8574c47836784923898408ca6c54bae9650dcf12"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.6/bin/windows/amd64/kubectl.exe",
          sha512Hash = "ea3f27423890c73faf040805bfe1a491bd3a4deb008bf7b6a05321a5f44d57405189a519406425fd42f2ba439cf40becc50511c18d2dd59efb00ee6edc562ac4"
        )
      )

  val V1_16_5: KubectlRelease = KubectlRelease(
        Version(1, 16, 5),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.5/bin/linux/amd64/kubectl",
          sha512Hash = "3d6099598820dbd1bb24ab910cee47f23500aa46fddd89c4f5d762bd025971ed6e683516df94e9bfc5d2fbbc490f53a8cb573f1621e239317999694618a5437b"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.5/bin/darwin/amd64/kubectl",
          sha512Hash = "f06e3a6ad33f60f73593ce2c5b8a1e8454b31d6ce9e95c4b4478bf57644c66ff937b47acc8ec0dc841e89113c68f09e7528a0cde23fcd774f5dd14c963372234"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.5/bin/windows/amd64/kubectl.exe",
          sha512Hash = "a13d32f30df051989680e6b796421fad7c57c6a2294bc5723d02376906ad1b97e87f36a8be045303abc036831aa42eda4389d9857b833cffbcc99c868ae5f532"
        )
      )

  val V1_16_4: KubectlRelease = KubectlRelease(
        Version(1, 16, 4),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.4/bin/linux/amd64/kubectl",
          sha512Hash = "1b7db20934de854c919d5131e0c0855b2923f8f7418e04fe0db06a63fccc0e42d29dfcb24faa0b87e498eb352a4cdbdef737618509bf2cd7f4184fe8591819ca"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.4/bin/darwin/amd64/kubectl",
          sha512Hash = "7a0a9a949873366f75a85017594481f5f6799bc970814728c788f814b584aceaf2160d4a7fc950fb2319811286d226c5feca1ad59763582c1010f2d2fa30c6fe"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.16.4/bin/windows/amd64/kubectl.exe",
          sha512Hash = "4c24d2589d0bbede9990e5612b09dd2c632df10097bfadbc536b246ba0c387a14fac3ce49b89679a6da6dc5facea2f8a61bab6521b5996af8ef5c161903c614f"
        )
      )

  val V1_15_12: KubectlRelease = KubectlRelease(
        Version(1, 15, 12),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.12/bin/linux/amd64/kubectl",
          sha512Hash = "08fd8a525dea12a9e3ea1fcbe552311bee579219617a95e3a5bbae2510bd1ec94227138bbba56aaf5571daab86e03d53b0a319d2f8eebe0abfc000e5ee1117d0"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.12/bin/darwin/amd64/kubectl",
          sha512Hash = "f9f001314d7dbf4f08cb73e16092d0a6105344563d0955c961763bccb0e7ca4ae4cd047e205153e1caf7a070e2b585464163991987bfe393e600ed9557a6a3fc"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.12/bin/windows/amd64/kubectl.exe",
          sha512Hash = "b9cd23710f0058420a931d9b25f583e232397812b88619ea0d94b42850d3bd7afbfc3ade0777e035e6a4321572444ec88e7e3746a6f13ef47267fbf7ce35d242"
        )
      )

  val V1_15_11: KubectlRelease = KubectlRelease(
        Version(1, 15, 11),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.11/bin/linux/amd64/kubectl",
          sha512Hash = "6973132f4f3269ccd995386de47740c6bab714a44f8382b67e18a87972533ed0c57fa9c0d6e45ec75fdfe3adf18da0eed67b9c8ff8777da7303bd59bea151905"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.11/bin/darwin/amd64/kubectl",
          sha512Hash = "0626fff10be69e289b0c20b2d77dfdf2d74940280a48c8c497d6e69cb532e820e41d99184aabcb0100de2f76175c5e2f4f748b3347542acdafa1819ff4ce118d"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.11/bin/windows/amd64/kubectl.exe",
          sha512Hash = "f9011670b6a671be8e270e8b40a6366cfc3c72fe3793b4f93c6b9c4955f6adb71f39aaa78d33de3251c597e4eeebd252722b7f85d8245daeb5158a38dae2b8a2"
        )
      )

  val V1_15_10: KubectlRelease = KubectlRelease(
        Version(1, 15, 10),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.10/bin/linux/amd64/kubectl",
          sha512Hash = "af95b5435158b7f59d18c923a41948a74bbc166fd5994889c2858c490f83e73fe8db17cc048d7e85d849929f5720f6d7aa0667482c263179db90f9e4f307d6c5"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.10/bin/darwin/amd64/kubectl",
          sha512Hash = "bca375d9d5090608456694545d19bdc0be300bd0799269b9feca9d9d795720c5f57f467954d04f26d79b25a44569c0b590c55bc45f92db3327dd669dc412be6e"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.10/bin/windows/amd64/kubectl.exe",
          sha512Hash = "fc39038e1a3c052a6c1e82cdcbc60b8b2818b6e5bf31463a138ad8db47ca13f265effe99f1943cdad81d988fc14b1c9d2ef9455b5778b9d553017433c9113285"
        )
      )

  val V1_15_9: KubectlRelease = KubectlRelease(
        Version(1, 15, 9),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.9/bin/linux/amd64/kubectl",
          sha512Hash = "130cbbc762d0d6d68f737adbe76ca909ade8a780b19bee53cf36edbfb27caeec1a15487e2fe1c396249ab1caa732cd2bc71fe32862b51083f784c7bdf93b982d"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.9/bin/darwin/amd64/kubectl",
          sha512Hash = "795e2625e0f7bf91b215ba6a1a34d4b52447cb36edaef5b8e2bf96691c3657e12b5b5ad7125ad8bdcf3d917eb3ec4f91f344d69d4f97cd357c0ff29c64d556ab"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.9/bin/windows/amd64/kubectl.exe",
          sha512Hash = "70f462097b0d3557859e15313cb695d2716a20d0e79210987fd07079836b0fa3ea27eb06782f3dcd7dfb73efe74bd7fa6f3dd05ecfa785a6c00bb1dd838847dc"
        )
      )

  val V1_15_8: KubectlRelease = KubectlRelease(
        Version(1, 15, 8),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.8/bin/linux/amd64/kubectl",
          sha512Hash = "58d3cec31a5dab6f62a2ddd3bce28bd1392f61ccb8ac3a043aef245b46bef31185281077152fbc9754778499c5e9adada48e972ff43dd8379288b4667042e0bb"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.8/bin/darwin/amd64/kubectl",
          sha512Hash = "db3f05daeaccf88bbf03fa50e6de2c256fcff9e5f1ffeed76892127bfa95317bfc8086528143dd8919fc68239d62595ff7d5ba4bb9cdbd9a39e9e4b5c5499d94"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.8/bin/windows/amd64/kubectl.exe",
          sha512Hash = "4eb2c421d94903194282f7af7980de37e3b176e632cecc231a1ea74dfc1892f8da2320ffddd61d7bcc5f2738d9444f5c30494c8b28004427ad9327e6de8c1e2b"
        )
      )

  val V1_15_7: KubectlRelease = KubectlRelease(
        Version(1, 15, 7),
        KubectlDistribution(
          operatingSystem = Linux,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.7/bin/linux/amd64/kubectl",
          sha512Hash = "c4bab9b2930d5e9ee7d88c2f100fc981cefd5be177baba23d5b674372972015aa8fb47189f75444699afdd5fda1ce8acb63dde136fd052bbae431a173c1ff41e"
        ),
        KubectlDistribution(
          operatingSystem = MacOs,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.7/bin/darwin/amd64/kubectl",
          sha512Hash = "74f9c0720c3357c4926ac2eb258aeb5206e0a724715fa7b84ab5f2f16272c1a8117d507af02fcf3e1c4b89ca74d8ad54d4c52bc8860d998693462a5990bf8bf1"
        ),
        KubectlDistribution(
          operatingSystem = Windows,
          downloadUrl = "https://storage.googleapis.com/kubernetes-release/release/v1.15.7/bin/windows/amd64/kubectl.exe",
          sha512Hash = "286b0b0f0c2d6ec92df397e4c5216d9da47e9e129ba151bdacfb2d803997f14a4e4256af5bfb847aa87c49e63b363000698d804e51455dde54ab738938144bac"
        )
      )

  val V1: KubectlRelease = V1_19_3

  val V1_15: KubectlRelease = V1_15_12

  val V1_16: KubectlRelease = V1_16_15

  val V1_17: KubectlRelease = V1_17_13

  val V1_18: KubectlRelease = V1_18_10

  val V1_19: KubectlRelease = V1_19_3
}
