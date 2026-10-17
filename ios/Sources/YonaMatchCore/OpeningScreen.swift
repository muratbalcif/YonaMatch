import SwiftUI

@available(iOS 16.0, *)
struct OpeningScreen: View {
    let splash: Bool
    let onSplashFinished: () -> Void
    let onStart: () -> Void

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                LinearGradient(
                    colors: [Color(red: 0.70, green: 0.91, blue: 1),
                             .white,
                             Color(red: 0.90, green: 0.97, blue: 0.75),
                             Color(red: 0.48, green: 0.82, blue: 0.55)],
                    startPoint: .top, endPoint: .bottom
                ).ignoresSafeArea()
                VStack {
                    HStack {
                        Text("☁️").font(.system(size: 78))
                        Spacer()
                        Text("☁️").font(.system(size: 72))
                    }
                    Spacer()
                    Text("🌿 🌼 🌱 🌸 🌿").font(.system(size: 32))
                }
                .padding(.bottom, 12)
                VStack(spacing: 16) {
                    Spacer(minLength: 30)
                    if splash {
                        Text("YonaMatch")
                            .font(.system(size: 52, weight: .heavy, design: .rounded))
                            .foregroundStyle(Color(red: 0.06, green: 0.50, blue: 0.84))
                        Text("Eşleştir · Keşfet · Öğren")
                            .font(.system(size: 18))
                            .foregroundStyle(.blue)
                    } else {
                        Text("Hoş Geldin!")
                            .font(.system(size: 38, weight: .heavy))
                            .foregroundStyle(Color(red: 0.07, green: 0.30, blue: 0.60))
                        Text("YonaMatch ile eğlenerek keşfet,\neşleştir ve öğren!")
                            .font(.system(size: 19))
                            .multilineTextAlignment(.center)
                            .foregroundStyle(Color(red: 0.07, green: 0.30, blue: 0.60))
                    }
                    Spacer()
                    Text("⭐").font(.system(size: 38))
                    Text("🦁").font(.system(size: min(geometry.size.width * 0.48, 180)))
                    if !splash {
                        Text("🐧   🐰   🐘").font(.system(size: 58))
                    }
                    Spacer()
                    if splash {
                        ProgressView()
                            .tint(.cyan)
                            .scaleEffect(1.4)
                        Text("Yükleniyor...")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundStyle(.white)
                    } else {
                        Button(action: onStart) {
                            HStack {
                                Spacer()
                                Text("Başla").font(.system(size: 24, weight: .bold))
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .font(.system(size: 24, weight: .bold))
                            }
                            .foregroundStyle(Color(red: 0.07, green: 0.30, blue: 0.60))
                            .padding(.horizontal, 28)
                            .frame(height: 68)
                            .background(Color(red: 1, green: 0.79, blue: 0.19),
                                        in: RoundedRectangle(cornerRadius: 30))
                        }
                    }
                }
                .padding(24)
            }
        }
        .onAppear {
            if splash {
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.4) {
                    onSplashFinished()
                }
            }
        }
    }
}
