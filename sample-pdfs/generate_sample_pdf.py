#!/usr/bin/env python3
"""
Generates a sample CMR contract PDF for testing.
Run: pip install reportlab && python3 generate_sample_pdf.py
"""

try:
    from reportlab.lib.pagesizes import A4
    from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
    from reportlab.lib.units import mm
    from reportlab.lib import colors
    from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
    from reportlab.lib.enums import TA_CENTER, TA_LEFT
except ImportError:
    print("Installing reportlab...")
    import subprocess, sys
    subprocess.check_call([sys.executable, "-m", "pip", "install", "reportlab"])
    from reportlab.lib.pagesizes import A4
    from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
    from reportlab.lib.units import mm
    from reportlab.lib import colors
    from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
    from reportlab.lib.enums import TA_CENTER, TA_LEFT

import os

OUTPUT = os.path.join(os.path.dirname(__file__), "ces-cmr-2024-0042.pdf")

def build():
    doc = SimpleDocTemplate(OUTPUT, pagesize=A4,
                            rightMargin=20*mm, leftMargin=20*mm,
                            topMargin=20*mm, bottomMargin=20*mm)
    styles = getSampleStyleSheet()

    dark = colors.HexColor('#1a1a2e')
    mid  = colors.HexColor('#4a4a6a')

    title_style = ParagraphStyle('Title', parent=styles['Title'],
        fontSize=14, textColor=dark, spaceAfter=4, alignment=TA_CENTER)
    sub_style   = ParagraphStyle('Sub', parent=styles['Normal'],
        fontSize=9, textColor=mid, spaceAfter=2, alignment=TA_CENTER)
    section_style = ParagraphStyle('Section', parent=styles['Normal'],
        fontSize=10, textColor=colors.white, spaceAfter=6,
        backColor=dark, leftIndent=-5, rightIndent=-5,
        borderPadding=(4, 6, 4, 6), fontName='Helvetica-Bold')
    label_style = ParagraphStyle('Label', parent=styles['Normal'],
        fontSize=9, textColor=mid, spaceAfter=2, fontName='Helvetica-Bold')
    value_style = ParagraphStyle('Value', parent=styles['Normal'],
        fontSize=9, textColor=dark, spaceAfter=6)

    story = []

    # Header
    story.append(Paragraph("GE AEROSPACE ENGINE SERVICES", title_style))
    story.append(Paragraph("Component Maintenance Report (CMR) — Maintenance Contract Agreement", sub_style))
    story.append(Paragraph("Document Ref: GEA-CES-CMR-2024-0042  |  Classification: CONFIDENTIAL", sub_style))
    story.append(HRFlowable(width="100%", thickness=2, color=dark, spaceAfter=10))

    # Section 1 - Contract Info
    story.append(Paragraph("1. CONTRACT INFORMATION", section_style))
    story.append(Spacer(1, 6))
    fields1 = [
        ("Contract Number:", "CMR-2024-GEA-CES-0042"),
        ("Work Order Number:", "WO-GEA-2024-108754"),
        ("Contract Type:", "Time & Material (T&M) — Engine Shop Visit"),
        ("Contract Date:", "March 15, 2024"),
        ("Effective Period:", "March 15, 2024 – September 15, 2024"),
    ]
    for label, value in fields1:
        row = Table([[Paragraph(label, label_style), Paragraph(value, value_style)]],
                    colWidths=[55*mm, None])
        row.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
        story.append(row)

    story.append(Spacer(1, 6))

    # Section 2 - Parties
    story.append(Paragraph("2. PARTIES", section_style))
    story.append(Spacer(1, 6))
    fields2 = [
        ("Service Provider:", "GE Aerospace Engine Services (CES), Cincinnati, OH 45215, USA"),
        ("Customer / Airline Operator:", "American Airlines, Inc."),
        ("Customer Contact:", "James T. Wilson, Director of Engineering"),
        ("Station Location:", "GE CES MRO – Cincinnati, Ohio (CVG)"),
    ]
    for label, value in fields2:
        row = Table([[Paragraph(label, label_style), Paragraph(value, value_style)]],
                    colWidths=[55*mm, None])
        row.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
        story.append(row)

    story.append(Spacer(1, 6))

    # Section 3 - Aircraft & Engine
    story.append(Paragraph("3. AIRCRAFT & ENGINE DETAILS", section_style))
    story.append(Spacer(1, 6))
    fields3 = [
        ("Aircraft Type:", "Boeing 737-800"),
        ("Aircraft Registration:", "N951AA"),
        ("Engine Model:", "CFM56-7B27"),
        ("Engine Serial Number:", "896-427"),
        ("Engine Position:", "#1 (Left Wing)"),
        ("Total Time Since New (TTSN):", "28,450 FH / 19,820 FC"),
        ("Shop Visit Date:", "March 20, 2024"),
    ]
    for label, value in fields3:
        row = Table([[Paragraph(label, label_style), Paragraph(value, value_style)]],
                    colWidths=[55*mm, None])
        row.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
        story.append(row)

    story.append(Spacer(1, 6))

    # Section 4 - Scope of Work
    story.append(Paragraph("4. SCOPE OF WORK", section_style))
    story.append(Spacer(1, 6))
    story.append(Paragraph("Work Scope: Performance Restoration Shop Visit (PRSV)", value_style))

    scope_data = [
        ['Task', 'Description', 'ATA Chapter'],
        ['HPC Blade Replacement', 'Replace Stage 3-9 HPC blades, blend limits exceeded', '72-30'],
        ['HPT Nozzle Repair', 'Braze repair on Stage 1 HPT nozzle segments', '72-60'],
        ['Combustor Inspection', 'Borescope + dimensional check, liner replacement', '72-40'],
        ['LPT Rotor Overhaul', 'Full teardown, disk inspection, blade re-coat', '72-50'],
        ['FADEC Software Update', 'Software update to version 5.2.7', '73-20'],
    ]
    scope_table = Table(scope_data, colWidths=[45*mm, 90*mm, 25*mm])
    scope_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), dark),
        ('TEXTCOLOR', (0,0), (-1,0), colors.white),
        ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'),
        ('FONTSIZE', (0,0), (-1,-1), 8),
        ('GRID', (0,0), (-1,-1), 0.5, mid),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor('#f5f5f5')]),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(scope_table)
    story.append(Spacer(1, 8))

    # Section 5 - Financials
    story.append(Paragraph("5. FINANCIALS", section_style))
    story.append(Spacer(1, 6))
    fin_fields = [
        ("Labor Charges:", "$1,245,000.00 USD"),
        ("Parts & Materials:", "$3,892,500.00 USD"),
        ("TAT Penalty Waiver:", "($45,000.00) USD"),
        ("Total Contract Value:", "$5,092,500.00 USD"),
        ("Currency:", "USD"),
        ("Return to Service Date:", "August 30, 2024"),
    ]
    for label, value in fin_fields:
        row = Table([[Paragraph(label, label_style), Paragraph(value, value_style)]],
                    colWidths=[55*mm, None])
        row.setStyle(TableStyle([('VALIGN', (0,0), (-1,-1), 'TOP')]))
        story.append(row)

    story.append(Spacer(1, 8))

    # Section 6 - Authorizations
    story.append(Paragraph("6. AUTHORIZATIONS", section_style))
    story.append(Spacer(1, 8))
    sig_data = [
        ['GE Aerospace CES — Authorized Representative', 'Customer — Authorized Signatory'],
        ['\n\n\n', '\n\n\n'],
        ['Sarah M. Chen, VP – MRO Operations', 'James T. Wilson, Director of Engineering'],
    ]
    sig_table = Table(sig_data, colWidths=[85*mm, 85*mm])
    sig_table.setStyle(TableStyle([
        ('FONTNAME', (0,0), (-1,0), 'Helvetica-Bold'),
        ('FONTSIZE', (0,0), (-1,-1), 8),
        ('GRID', (0,0), (-1,-1), 0.5, mid),
        ('VALIGN', (0,0), (-1,-1), 'BOTTOM'),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
        ('LINEABOVE', (0,2), (-1,2), 0.5, dark),
    ]))
    story.append(sig_table)

    doc.build(story)
    print(f"✅ Sample PDF generated: {OUTPUT}")

if __name__ == "__main__":
    build()
